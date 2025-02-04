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

    //List<Order> findByCart_CartId(Integer cartId);
    Page<Order> findByCart_CartId(Integer cartId, Pageable pageable);

    Order findByOrderId(Integer orderId);

    // Count orders with PENDING status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'PENDING'")
    long countPendingOrders();

    // Total revenue
    @Query("SELECT SUM(o.totalCost) FROM Order o WHERE o.orderStatus IN ('DELIVERED', 'CONFIRMED', 'SHIPPED')")
    Double calculateTotalRevenue();

    // Count orders with DELIVERED status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'DELIVERED'")
    long countDeliveredOrders();

    // Count orders with CANCELLED status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'CANCELLED'")
    long countCancelledOrders();
}
