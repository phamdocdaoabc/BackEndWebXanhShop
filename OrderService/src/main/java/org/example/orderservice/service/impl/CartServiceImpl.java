package org.example.orderservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.constant.AppConstant;
import org.example.orderservice.domain.dtos.CartDTO;
import org.example.orderservice.domain.dtos.CartItemRequest;
import org.example.orderservice.domain.dtos.UserDTO;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.CartItem;
import org.example.orderservice.exception.CartNotFoundException;
import org.example.orderservice.mappers.CartMapping;
import org.example.orderservice.repository.CartItemRepository;
import org.example.orderservice.repository.CartRepository;
import org.example.orderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;
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
}
