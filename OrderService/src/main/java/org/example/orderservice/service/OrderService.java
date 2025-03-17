package org.example.orderservice.service;

import org.example.orderservice.domain.dtos.OrderAdmin.OrderMonthlyStatisticDTO;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderQuarterlyStatisticDTO;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderStatisticsDTO;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderYearlyStatisticDTO;
import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.example.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

    long getPendingOrderCount();
    Double getTotalRevenue();
    long getDeliveredOrderCount();
    long getCancelledOrderCount();

    Page<Order> getAllOrders(Pageable pageable);

    void updateOrderStatus(Integer orderId, String newStatus);

    Page<OrderStatisticsDTO> getOrderStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<OrderYearlyStatisticDTO> getOrderYearStatistics(int year, Pageable pageable);

    Page<OrderMonthlyStatisticDTO> getOrderMonthStatistics(int year, Pageable pageable);

    Page<OrderQuarterlyStatisticDTO> getOrderStatisticsByQuarter(int year, Pageable pageable);

}
