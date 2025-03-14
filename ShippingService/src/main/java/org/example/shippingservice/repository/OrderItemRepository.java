package org.example.shippingservice.repository;


import org.example.shippingservice.domain.entity.OrderItem;
import org.example.shippingservice.domain.entity.OrderItemId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findByOrderId(Integer orderId);
    Page<OrderItem> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
