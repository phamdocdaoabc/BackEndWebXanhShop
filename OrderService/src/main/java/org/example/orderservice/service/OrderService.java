package org.example.orderservice.service;

import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    List<OrderDTO> findAll();
    OrderDTO findById(Integer orderId);
    OrderDTO save(OrderDTO orderDTO);

    OrderDTO update(OrderDTO orderDTO);
    OrderDTO update(Integer orderId,OrderDTO orderDTO);
    void deleteById(Integer orderId);

    //List<OrderResponse> getOrderDetailsByUserId(Integer userId);

    Page<OrderResponse> getOrderDetailsByUserId(Integer userId, int page, int size);

}
