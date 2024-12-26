package org.example.orderservice.controller;

import org.example.orderservice.domain.dtos.cart.CartItemRequest;
import org.example.orderservice.domain.dtos.cart.CartResponse;
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

    // API Lấy thông tin sản phẩm trong giỏ hàng dựa vào userId
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCartByUserId(@PathVariable Integer userId) {
        try {
            CartResponse cartResponse = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cartResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy giỏ hàng: " + e.getMessage());
        }
    }

    // Endpoint xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProductFromCart(@RequestParam("userId") Integer userId,
                                                        @RequestParam("productId") Integer productId) {
        try {
            boolean isRemoved = cartService.removeProductFromCart(userId, productId);
            if (isRemoved) {
                return ResponseEntity.ok("Sản phẩm đã được xóa khỏi giỏ hàng.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy sản phẩm trong giỏ hàng của người dùng.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi xóa sản phẩm.");
        }
    }
}
