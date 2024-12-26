package org.example.orderservice.repository;

import com.linecorp.armeria.server.annotation.Param;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.example.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Tạo phương thức để tìm kiếm đơn hàng theo userId

    //List<Order> findByCart_CartId(Integer cartId);
    Page<Order> findByCart_CartId(Integer cartId, Pageable pageable);
}
