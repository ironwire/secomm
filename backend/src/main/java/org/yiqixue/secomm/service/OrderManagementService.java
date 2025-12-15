package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.dto.OrderStatusUpdateRequest;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.entity.Order;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.mapper.OrderMapper;
import org.yiqixue.secomm.repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * 获取所有订单（分页）
     */
    public PageResponse<OrderDTO> getAllOrders(int page, int size, String sortBy, String sortDir) {
        log.info("获取所有订单 - 页码: {}, 大小: {}, 排序: {} {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.findAllWithCustomer(pageable);

        return orderMapper.toPageResponse(orderPage);
    }

    /**
     * 根据状态获取订单（分页）
     */
    public PageResponse<OrderDTO> getOrdersByStatus(String status, int page, int size, String sortBy, String sortDir) {
        log.info("根据状态获取订单 - 状态: {}, 页码: {}, 大小: {}", status, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        Page<Order> orderPage = orderRepository.findByStatusWithCustomer(orderStatus, pageable);

        return orderMapper.toPageResponse(orderPage);
    }

    /**
     * 搜索订单
     */
    public PageResponse<OrderDTO> searchOrders(String query, int page, int size, String sortBy, String sortDir) {
        log.info("搜索订单 - 关键词: {}, 页码: {}, 大小: {}", query, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.searchOrders(query, pageable);

        return orderMapper.toPageResponse(orderPage);
    }

    /**
     * 根据ID获取订单详情
     */
    public OrderDTO getOrderById(Long orderId) {
        log.info("获取订单详情 - 订单ID: {}", orderId);

        Order order = orderRepository.findByIdWithCustomerAndItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return orderMapper.toDTO(order);
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        log.info("更新订单状态 - 订单ID: {}, 新状态: {}", orderId, request.getStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.getStatus().toUpperCase());
        order.setStatus(newStatus);
        
        order = orderRepository.save(order);

        log.info("订单状态更新成功 - 订单ID: {}, 状态: {}", orderId, newStatus);

        return orderMapper.toDTO(order);
    }

    /**
     * 获取订单统计信息
     */
    public Map<String, Object> getOrderStats() {
        log.info("获取订单统计信息");

        Map<String, Object> stats = new HashMap<>();
        
        // 总订单数
        long totalOrders = orderRepository.count();
        stats.put("totalOrders", totalOrders);
        
        // 各状态订单数
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            stats.put(status.name().toLowerCase() + "Orders", count);
        }
        
        // 今日订单数
        long todayOrders = orderRepository.countTodayOrders();
        stats.put("todayOrders", todayOrders);
        
        // 本月订单数
        long monthOrders = orderRepository.countMonthOrders();
        stats.put("monthOrders", monthOrders);

        return stats;
    }
}