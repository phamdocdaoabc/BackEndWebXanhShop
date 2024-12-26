package org.example.orderservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.constant.AppConstant;
import org.example.orderservice.domain.dtos.*;
import org.example.orderservice.domain.dtos.cart.CartItemRequest;
import org.example.orderservice.domain.dtos.cart.CartItemResponse;
import org.example.orderservice.domain.dtos.cart.CartResponse;
import org.example.orderservice.domain.dtos.cart.ProductResponse;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.CartItem;
import org.example.orderservice.exception.CartNotFoundException;
import org.example.orderservice.mappers.CartMapping;
import org.example.orderservice.repository.CartItemRepository;
import org.example.orderservice.repository.CartRepository;
import org.example.orderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor

public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    private final RestTemplate restTemplate;

    @Override
    public List<CartDTO> findAll() {

        log.info("CartServiceImplementation, find all the CartDTOS");
        return this.cartRepository.findAll()
                .stream()
                .map(CartMapping::map)
                .map(cart -> {
                    // Capture restTemplate in a local variable
                    cart.setUserDTO(this.restTemplate.getForObject(
                            AppConstant.DiscoveredDomainsApi.USER_SERVICE_API_URL + "/" + cart.getUserDTO().getUserId(),
                            UserDTO.class));
                    return cart;
                })
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }


    @Override
    public CartDTO findById(Integer cartId) {
        log.info("CartServiceImplementation,Find the CartDTo by using cart Id");
        return this.cartRepository.findById(cartId)
                .map(CartMapping::map)
                .map(cart -> {
                    cart.setUserDTO(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .USER_SERVICE_API_URL + "/" + cart.getUserDTO().getUserId(), UserDTO.class));
                    return cart;

                })
                .orElseThrow(() -> new CartNotFoundException(String.format("Cart with id: %d is not found", cartId)));

    }

    @Override
    public CartDTO save(CartDTO cartDTO) {
        log.info("CartServiceImplementation, Save the cartDTOs");
        return CartMapping.map(this.cartRepository
                .save(CartMapping.map(cartDTO)));
    }

    @Override
    public CartDTO update(CartDTO cartDTO) {
        log.info("CartServiceImplementation, Update the CartDTO");
        return CartMapping.map(this.cartRepository
                .save(CartMapping.map(cartDTO)));
    }

    @Override
    public CartDTO update(Integer cartId, CartDTO cartDTO) {
        log.info("CartServiceImplementation, Update the cartDTO using cartID");
        return CartMapping.map(this.cartRepository
                .save(CartMapping.map(findById(cartId))));
    }

    @Override
    public void deleteById(Integer cartId) {
        log.info("CartServiceImplementaion, Delete the cartDTO using cartId");
        this.cartRepository.deleteById(cartId);

    }

    @Autowired
    private CartItemRepository cartItemRepository;

    /*@Override
    public Integer getUserIdFromToken(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "http://localhost:9056/user-service/api/profile/infor",
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            Object userIdObj = response.getBody().get("userId");  // Lấy giá trị của userId từ respons
            if (userIdObj != null) {
                return Integer.parseInt(userIdObj.toString());
            }else {
                throw new RuntimeException("Không tìm thấy userId trong response");
            }

            // Lấy userId từ response và chuyển đổi
            // return Integer.parseInt(response.getBody().get("userId").toString());

        } catch (Exception e) {
            // In chi tiết lỗi để debug
            throw new RuntimeException("Không thể lấy userId từ token: " + e.getMessage(), e);
        }
    }*/

    @Override
    public void addItemToCart(Integer userId, CartItemRequest request) {
        // 1. Tìm giỏ hàng của người dùng
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        // 2. Tìm cartItem dựa trên cartId và productId
        CartItem cartItem = cartItemRepository.findByCart_CartIdAndProductId(cart.getCartId(), request.getProductId())
                .orElseGet(() -> new CartItem(cart, request.getProductId(), 0, request.getPrice()));

        // 3. Cập nhật số lượng và giá của sản phẩm
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setPrice(request.getPrice());

        // 4. Lưu lại cartItem
        cartItemRepository.save(cartItem);
    }

    // Lấy thông tin sản phẩm trong giỏ hàng dựa vào userId
    @Override
    public CartResponse getCartByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));

        List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());

        // Lấy danh sách productId từ cartItem
        List<Integer> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        System.out.println("Danh sách productIds: " + productIds);
        // Gọi product-service để lấy thông tin chi tiết sản phẩm
        String productServiceUrl = "http://localhost:9056/product-service/api/products/details";
        ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(
                productServiceUrl,
                HttpMethod.POST,
                new HttpEntity<>(productIds),
                new ParameterizedTypeReference<List<ProductResponse>>() {}
        );

        List<ProductResponse> productDetails = response.getBody();

        // Kết hợp thông tin giỏ hàng và chi tiết sản phẩm
        List<CartItemResponse> cartItemResponses = cartItems.stream().map(cartItem -> {
            ProductResponse product = productDetails.stream()
                    .filter(p -> p.getProductId().equals(cartItem.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
            return new CartItemResponse(
                    cartItem.getProductId(),
                    product.getProductName(),
                    product.getImageUrl(),
                    cartItem.getQuantity(),
                    product.getPrice(),
                    product.getDiscount(),
                    product.getCategoryName()
            );
        }).collect(Collectors.toList());
        // Tính tổng giá trị giỏ hàng (totalPrice)
        double totalPrice = cartItemResponses.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice() * (1 - item.getDiscount() / 100.0))
                .sum();

        int totalItems = (int) cartItems.stream().map(CartItem::getProductId).distinct().count();

        // Trả về thông tin giỏ hàng
        return new CartResponse(cart.getCartId(), cartItemResponses, totalItems, totalPrice);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @Override
    public boolean removeProductFromCart(Integer userId, Integer productId) {
        // Tìm cartId của người dùng
        Cart cart = cartRepository.findByUserId(userId) .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));
        if (cart != null) {
            // Tìm sản phẩm trong giỏ hàng
            CartItem cartItem = cartItemRepository.findByCart_CartIdAndProductId(cart.getCartId(), productId)
                    .orElseThrow(() -> new RuntimeException("Không có sản phẩm phù hợp trong giỏ hàng!"));
            if (cartItem != null) {
                // Xóa sản phẩm khỏi giỏ hàng
                cartItemRepository.delete(cartItem);
                return true;
            }
        }
        return false;
    }
}
