package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.ApiResponse;
import org.yiqixue.secomm.dto.OrderDTO;
import org.yiqixue.secomm.dto.OrderStatusUpdateRequest;
import org.yiqixue.secomm.dto.PageResponse;
import org.yiqixue.secomm.service.OrderManagementService;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "订单管理", description = "管理员订单管理相关的API接口")
@CrossOrigin(origins = "http://localhost:5173")
//@PreAuthorize("hasRole('ADMIN')")
public class OrderManagementController {

    private final OrderManagementService orderManagementService;

    /**
     * 获取所有订单（分页）
     */
    @GetMapping
    @Operation(summary = "获取所有订单", description = "管理员分页获取所有订单列表")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getAllOrders(
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 管理员获取所有订单: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);

        PageResponse<OrderDTO> orders = orderManagementService.getAllOrders(page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 根据状态获取订单（分页）
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取订单", description = "管理员根据状态分页获取订单列表")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrdersByStatus(
            @Parameter(description = "订单状态")
            @PathVariable String status,
            
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 按状态获取订单: status={}, page={}, size={}", status, page, size);

        PageResponse<OrderDTO> orders = orderManagementService.getOrdersByStatus(status, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 搜索订单
     */
    @GetMapping("/search")
    @Operation(summary = "搜索订单", description = "管理员根据关键词搜索订单")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> searchOrders(
            @Parameter(description = "搜索关键词（订单号、客户姓名、邮箱）")
            @RequestParam String query,
            
            @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "排序字段")
            @RequestParam(defaultValue = "dateCreated") String sortBy,
            
            @Parameter(description = "排序方向：asc或desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("API调用 - 搜索订单: query={}, page={}, size={}", query, page, size);

        PageResponse<OrderDTO> orders = orderManagementService.searchOrders(query, page, size, sortBy, sortDir);

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    /**
     * 根据订单ID获取订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @Parameter(description = "订单ID")
            @PathVariable Long orderId) {

        log.info("API调用 - 获取订单详情: orderId={}", orderId);

        OrderDTO order = orderManagementService.getOrderById(orderId);

        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "管理员更新订单状态")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @Parameter(description = "订单ID")
            @PathVariable Long orderId,
            
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        log.info("API调用 - 更新订单状态: orderId={}, status={}", orderId, request.getStatus());

        try {
            OrderDTO order = orderManagementService.updateOrderStatus(orderId, request);
            
            return ResponseEntity.ok(ApiResponse.success("订单状态更新成功", order));
            
        } catch (Exception e) {
            log.error("更新订单状态失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取订单统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取订单统计", description = "管理员获取订单统计信息")
    public ResponseEntity<ApiResponse<Object>> getOrderStats() {
        log.info("API调用 - 获取订单统计");

        Object stats = orderManagementService.getOrderStats();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}