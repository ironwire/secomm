package org.yiqixue.secomm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.CheckoutRequest;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.CustomerService;
import org.yiqixue.secomm.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    private final CustomerService customerService;

    /**
     * 结算购物车，创建订单
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CheckoutRequest request) {
        
        log.info("API调用 - 结算: 用户ID={}, 总金额={}", 
                userPrincipal.getId(), request.getTotalAmount());

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        OrderDTO order = orderService.createOrderFromCart(customer.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
    }

    /**
     * 获取当前用户的订单列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("API调用 - 获取用户订单: 用户ID={}, page={}, size={}", 
                userPrincipal.getId(), page, size);

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        PageResponse<OrderDTO> orders = orderService.getUserOrders(customer.getId(), page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 根据订单ID获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId) {
        
        log.info("API调用 - 获取订单详情: 用户ID={}, 订单ID={}", 
                userPrincipal.getId(), orderId);

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        OrderDTO order = orderService.getUserOrderById(customer.getId(), orderId);

        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * 根据状态获取用户订单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getUserOrdersByStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("API调用 - 按状态获取用户订单: 用户ID={}, 状态={}, page={}, size={}", 
                userPrincipal.getId(), status, page, size);

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        PageResponse<OrderDTO> orders = orderService.getUserOrdersByStatus(customer.getId(), status, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
