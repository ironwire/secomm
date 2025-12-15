package org.yiqixue.secomm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.yiqixue.secomm.dto.*;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.security.UserPrincipal;
import org.yiqixue.secomm.service.CartService;
import org.yiqixue.secomm.service.CustomerService;

import java.util.List;

/**
 * 购物车控制器
 * 提供购物车相关的REST API接口
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "购物车管理", description = "购物车相关的API接口")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService;

    /**
     * 获取当前用户的购物车
     */
    @GetMapping
    @Operation(summary = "获取购物车", description = "获取当前用户的购物车信息")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("API调用 - 获取购物车: 用户ID={}", userPrincipal.getId());

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        CartDTO cart = cartService.getCartByCustomerId(customer.getId());

        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    /**
     * 获取购物车商品项列表
     */
    @GetMapping("/items")
    @Operation(summary = "获取购物车商品项", description = "获取当前用户购物车中的所有商品项")
    public ResponseEntity<ApiResponse<List<CartItemDTO>>> getCartItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("API调用 - 获取购物车商品项: 用户ID={}", userPrincipal.getId());

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

//        CartItemDTO cartItem = cartService.addToCart(customer.getId(), request);

        List<CartItemDTO> cartItems = cartService.getCartItems(customer.getId());

        return ResponseEntity.ok(ApiResponse.success(cartItems));
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    @Operation(summary = "添加商品到购物车", description = "将指定商品添加到当前用户的购物车")
    public ResponseEntity<ApiResponse<CartItemDTO>> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddToCartRequest request) {
        
        log.info("API调用 - 添加商品到购物车: 用户ID={}, 商品ID={}, 数量={}", 
                userPrincipal.getId(), request.getProductId(), request.getQuantity());
        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

        CartItemDTO cartItem = cartService.addToCart(customer.getId(), request);

        return ResponseEntity.ok(ApiResponse.success(cartItem));
    }

    /**
     * 更新购物车商品项数量
     */
    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "更新购物车商品项", description = "更新购物车中指定商品项的数量")
    public ResponseEntity<ApiResponse<CartItemDTO>> updateCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "购物车商品项ID")
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        
        log.info("API调用 - 更新购物车商品项: 用户ID={}, 商品项ID={}, 新数量={}", 
                userPrincipal.getId(), cartItemId, request.getQuantity());

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

//        CartItemDTO cartItem = cartService.addToCart(customer.getId(), request);

        CartItemDTO cartItem = cartService.updateCartItem(
                customer.getId(), cartItemId, request);

        return ResponseEntity.ok(ApiResponse.success(cartItem));
    }

    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "移除购物车商品", description = "从购物车中移除指定的商品项")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "购物车商品项ID")
            @PathVariable Long cartItemId) {
        
        log.info("API调用 - 移除购物车商品: 用户ID={}, 商品项ID={}", 
                userPrincipal.getId(), cartItemId);

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

//        CartItemDTO cartItem = cartService.addToCart(customer.getId(), request);

        cartService.removeFromCart(customer.getId(), cartItemId);

        return ResponseEntity.ok(ApiResponse.success(null, "商品已从购物车中移除"));
    }

    /**
     * 清空购物车
     */
    @DeleteMapping
    @Operation(summary = "清空购物车", description = "清空当前用户购物车中的所有商品")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        log.info("API调用 - 清空购物车: 用户ID={}", userPrincipal.getId());

        Customer customer = customerService.getCustomerByUserId(userPrincipal.getId());

//        CartItemDTO cartItem = cartService.addToCart(customer.getId(), request);

        cartService.clearCart(customer.getId());

        return ResponseEntity.ok(ApiResponse.success(null, "购物车已清空"));
    }
}