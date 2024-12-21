package org.example.orderservice.controller;

import org.example.orderservice.domain.dtos.CartItemRequest;
import org.example.orderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/test")
    public String test() {
        return "Service is running";
    }

    // API thêm sản phẩm vào giỏ hàng
    @PostMapping("/add-item/{userId}")
    public ResponseEntity<String> addItemToCart(@PathVariable Integer userId, @RequestBody CartItemRequest cartItemRequest) {
        try {
            // 2. Thêm sản phẩm vào giỏ hàng
            cartService.addItemToCart(userId, cartItemRequest);
            return ResponseEntity.ok("Thêm sản phẩm vào giỏ hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
    }

}
