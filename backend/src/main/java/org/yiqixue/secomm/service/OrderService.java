package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.CheckoutRequest;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.entity.Order;
import org.yiqixue.secomm.entity.OrderItem;
import org.yiqixue.secomm.entity.Product;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.mapper.OrderMapper;
import org.yiqixue.secomm.repository.CustomerRepository;
import org.yiqixue.secomm.repository.OrderRepository;
import org.yiqixue.secomm.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.yiqixue.secomm.dto.PageResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public OrderDTO createOrderFromCart(Long customerId, CheckoutRequest request) {
        log.info("创建订单 - 用户ID: {}, 总金额: {}", customerId, request.getTotalAmount());

        // 生成订单号
        String orderNumber = generateOrderNumber();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        // 创建订单
        Order order = Order.builder()
                .customerId(customerId)
                .orderNumber(orderNumber)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(request.getTotalAmount())
                .build();

        order = orderRepository.save(order);

        // 创建订单项
        Order finalOrder = order;
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

                    return OrderItem.builder()
                            .order(finalOrder)
                            .productId(item.getProductId())
                            .productName(product.getName())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        // 清空购物车
        cartService.clearCart(customerId);

        log.info("订单创建成功 - 订单号: {}", orderNumber);

        return orderMapper.toDTO(order);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int)(Math.random() * 1000));
        return "ORD" + timestamp + random;
    }

    /**
     * 获取用户订单列表（分页）
     */
    public PageResponse<OrderDTO> getUserOrders(Long customerId, int page, int size, String sortBy, String sortDir) {
        log.info("获取用户订单列表 - 客户ID: {}, 页码: {}, 大小: {}", customerId, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderPage = orderRepository.findByCustomerIdWithCustomer(customerId, pageable);

        return orderMapper.toPageResponse(orderPage);
    }

    /**
     * 根据订单ID获取用户订单详情
     */
    public OrderDTO getUserOrderById(Long customerId, Long orderId) {
        log.info("获取用户订单详情 - 客户ID: {}, 订单ID: {}", customerId, orderId);

        Order order = orderRepository.findByIdAndCustomerIdWithItems(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return orderMapper.toDTO(order);
    }

    /**
     * 根据状态获取用户订单
     */
    public PageResponse<OrderDTO> getUserOrdersByStatus(Long customerId, String status, int page, int size, String sortBy, String sortDir) {
        log.info("根据状态获取用户订单 - 客户ID: {}, 状态: {}, 页码: {}, 大小: {}", customerId, status, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : 
                Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        Page<Order> orderPage = orderRepository.findByCustomerIdAndStatusWithCustomer(customerId, orderStatus, pageable);

        return orderMapper.toPageResponse(orderPage);
    }
}
