package org.yiqixue.secomm.mapper;

import org.springframework.stereotype.Component;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.dto.OrderItemDTO;
import org.yiqixue.secomm.entity.Order;
import org.yiqixue.secomm.entity.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.yiqixue.secomm.dto.PageResponse;

/**
 * 订单 Entity 和 DTO 之间的转换工具类
 */
@Component
public class OrderMapper {

    /**
     * Order Entity 转 DTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getDateCreated())
                .customerName(order.getCustomer() != null ? 
                    order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName() : null)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .orderItems(order.getOrderItems() != null ? 
                    order.getOrderItems().stream()
                        .map(this::toOrderItemDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    /**
     * OrderItem Entity 转 DTO
     */
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }

    /**
     * Order Entity List 转 DTO List
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Page<Order> 转 PageResponse<OrderDTO>
     */
    public PageResponse<OrderDTO> toPageResponse(Page<Order> orderPage) {
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PageResponse.<OrderDTO>builder()
                .content(orderDTOs)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .first(orderPage.isFirst())
                .last(orderPage.isLast())
                .empty(orderPage.isEmpty()).build();
    }
}
