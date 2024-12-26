package org.example.orderservice.domain.dtos.cart;

import java.util.List;

public class CartResponse {
    private List<CartItemResponse> cartItems;
    private int totalItems;
    private Integer cartId;
    private double totalPrice;

    public CartResponse(Integer cartId, List<CartItemResponse> cartItems, int totalItems, double totalPrice) {
        this.cartId = cartId;
        this.cartItems = cartItems;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }

    public List<CartItemResponse> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemResponse> cartItems) {
        this.cartItems = cartItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
