package org.example.orderservice.repository;

import org.example.orderservice.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // Tìm kiếm CartItem dựa trên cartId và productId
    Optional<CartItem> findByCart_CartIdAndProductId(Integer cartId, Integer productId);

    List<CartItem> findByCart_CartId(Integer cartId);
}
