package org.example.shippingservice.service;

import org.example.shippingservice.domain.dtos.OrderItemsRequest;
import org.example.shippingservice.domain.dtos.adminStatistics.CategoryStatisticDTO;
import org.example.shippingservice.domain.dtos.adminStatistics.ProductStatisticDTO;
import org.example.shippingservice.domain.entity.OrderItemId;
import org.example.shippingservice.domain.dtos.OrderItemsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemService {
    List<OrderItemsDTO> findAll();
    OrderItemsDTO findById(final OrderItemId orderItemId);
    OrderItemsDTO save(final OrderItemsDTO orderItemDto);
    OrderItemsDTO update(final OrderItemsDTO orderItemDto);
    void deleteById(final OrderItemId orderItemId);

    void addOrderItems(Integer orderId, List<OrderItemsRequest.OrderItems> orderItems);

    Page<ProductStatisticDTO> getProductStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<CategoryStatisticDTO> getCategoryStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
