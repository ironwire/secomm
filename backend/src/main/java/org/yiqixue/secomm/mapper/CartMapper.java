package org.yiqixue.secomm.mapper;

import org.springframework.stereotype.Component;
import org.yiqixue.secomm.dto.CartDTO;
import org.yiqixue.secomm.dto.CartItemDTO;
import org.yiqixue.secomm.entity.Cart;
import org.yiqixue.secomm.entity.CartItem;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车 Entity 和 DTO 之间的转换工具类
 */
@Component
public class CartMapper {

    /**
     * Cart Entity 转 DTO
     */
    public CartDTO toDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomerId())
                .status(cart.getStatus().name())
                .totalPrice(cart.getTotalPrice())
                .totalQuantity(cart.getTotalQuantity())
                .dateCreated(cart.getDateCreated())
                .lastUpdated(cart.getLastUpdated())
                .cartItems(cart.getCartItems() != null ? 
                    cart.getCartItems().stream()
                        .map(this::toCartItemDTO)
                        .collect(Collectors.toList()) : null)
                .customerName(cart.getCustomer() != null ? 
                    cart.getCustomer().getFirstName() + " " + cart.getCustomer().getLastName() : null)
                .build();
    }

    /**
     * CartItem Entity 转 DTO
     */
    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        return CartItemDTO.builder()
                .id(cartItem.getId())
                .cartId(cartItem.getCartId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .subtotal(cartItem.getSubtotal())
                .dateAdded(cartItem.getDateAdded())
                .lastUpdated(cartItem.getLastUpdated())
                .productName(cartItem.getProduct() != null ? cartItem.getProduct().getName() : null)
                .productSku(cartItem.getProduct() != null ? cartItem.getProduct().getSku() : null)
                .productImageUrl(cartItem.getProduct() != null ? cartItem.getProduct().getImageUrl() : null)
                .productDescription(cartItem.getProduct() != null ? cartItem.getProduct().getDescription() : null)
                .unitsInStock(cartItem.getProduct() != null ? cartItem.getProduct().getUnitsInStock() : null)
                .build();
    }

    /**
     * CartItem Entity List 转 DTO List
     */
    public List<CartItemDTO> toCartItemDTOList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList());
    }
}