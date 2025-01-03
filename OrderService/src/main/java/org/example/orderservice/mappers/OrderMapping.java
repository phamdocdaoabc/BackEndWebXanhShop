package org.example.orderservice.mappers;

import org.example.orderservice.domain.dtos.CartDTO;
import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.Order;

import java.time.LocalDate;

public interface OrderMapping {
    public static OrderDTO map(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate().atStartOfDay())
                .orderDesc(order.getOrderDesc())
                .cartDTO(CartDTO.builder()
                        .cartId(order.getCart().getCartId())
                        .build())
                .build();
    }

    public static Order map(OrderDTO orderDTO) {
        return Order.builder()
                .orderId(orderDTO.getOrderId())
                .orderDate(LocalDate.from(orderDTO.getOrderDate()))
                .orderDesc(orderDTO.getOrderDesc())
                .cart(Cart.builder()
                        .cartId(orderDTO.getCartDTO().getCartId())
                        .build())
                .build();
    }
}
