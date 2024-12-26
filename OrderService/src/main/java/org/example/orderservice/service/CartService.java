package org.example.orderservice.service;

import org.example.orderservice.domain.dtos.CartDTO;
import org.example.orderservice.domain.dtos.cart.CartItemRequest;
import org.example.orderservice.domain.dtos.cart.CartResponse;

import java.util.List;

public interface CartService {
    List<CartDTO> findAll();
    CartDTO findById(Integer orderId);

    CartDTO save(CartDTO cartDTO);
    CartDTO update(CartDTO cartDTO);
    CartDTO update(Integer cartId, CartDTO cartDTO);

    void deleteById(Integer cartId);
    //Integer getUserIdFromToken(String token);

    // Thêm sản phẩm vào giỏ hàng
    void addItemToCart(Integer userId, CartItemRequest request);

    // lấy thông tin sản phẩm trong giỏ hàng
    CartResponse getCartByUserId(Integer userId);

    // Xóa sản phẩm khỏi giỏ hàng
    boolean removeProductFromCart(Integer userId, Integer productId);

}
