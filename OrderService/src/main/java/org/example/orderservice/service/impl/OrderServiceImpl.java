package org.example.orderservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.Order;
import org.example.orderservice.exception.CartNotFoundException;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mappers.OrderMapping;
import org.example.orderservice.repository.CartRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;


    @Override
    public List<OrderDTO> findAll()  {
        log.info("*** OrderDto List, service; fetch all orders *");
        return this.orderRepository.findAll()
                .stream()
                .map(OrderMapping::map)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public OrderDTO findById(Integer orderId) {
        log.info("*** OrderDto, service; fetch order by id *");
        return this.orderRepository.findById(orderId)
                .map(OrderMapping::map)
                .orElseThrow(() -> new OrderNotFoundException(String
                        .format("Order with id: %d not found", orderId)));
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.info("*** OrderDto, service; save order *");
        return OrderMapping.map(this.orderRepository
                .save(OrderMapping.map(orderDTO)));
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        log.info("*** OrderDto, service; update order *");
        return OrderMapping.map(this.orderRepository
                .save(OrderMapping.map(orderDTO)));
    }

    @Override
    public OrderDTO update(Integer orderId, OrderDTO orderDTO) {
        log.info("*** OrderDto, service; update order with orderId *");
        return OrderMapping.map(this.orderRepository
                .save(OrderMapping.map(this.findById(orderId))));
    }

    @Override
    public void deleteById(Integer orderId) {
        log.info("*** Void, service; delete order by id *");
        this.orderRepository.delete(OrderMapping.map(this.findById(orderId)));
    }

    @Autowired
    private final CartRepository cartRepository;

    /*
    @Override
    public List<OrderResponse> getOrderDetailsByUserId(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)  .orElseThrow(() -> new CartNotFoundException("cart not found with id: " + userId));
        // Gọi tất cả orders liên quan tới user
        List<Order> orderNew = orderRepository.findByCart_CartId(cart.getCartId());

        // Lấy isPayed từ PaymentService
        return orderNew.stream().map(order -> {
            String paymentUrl = "http://localhost:9056/payment-service/api/payments/" + order.getOrderId() + "/status";
            Boolean isPayed = restTemplate.getForObject(paymentUrl, Boolean.class);
            System.out.println("isPayed:"+ isPayed);
            // Tạo OrderResponse từ Order và isPayed
            return new OrderResponse(
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getShippingAddress(),
                    order.getPhone(),
                    order.getOrderStatus(),
                    isPayed,
                    order.getTotalCost()
            );
        }).collect(Collectors.toList());
    }
     */

    // list đơn hàng phân trang
    @Override
    public Page<OrderResponse> getOrderDetailsByUserId(Integer userId, int page, int size) {
        // Tìm cart liên quan tới user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + userId));

        // Cấu hình phân trang (page, size, sort theo orderDate giảm dần)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));

        // Tìm các orders liên quan tới cartId theo trang
        Page<Order> ordersPage = orderRepository.findByCart_CartId(cart.getCartId(), pageable);

        // Chuyển đổi từ Order sang OrderResponse
        return ordersPage.map(order -> {
            String paymentUrl = "http://localhost:9056/payment-service/api/payments/" + order.getOrderId() + "/status";
            Boolean isPayed = restTemplate.getForObject(paymentUrl, Boolean.class);
            System.out.println("isPayed: " + isPayed);

            // Tạo OrderResponse
            return new OrderResponse(
                    order.getOrderId(),
                    order.getOrderDate(),
                    order.getShippingAddress(),
                    order.getPhone(),
                    order.getOrderStatus(),
                    isPayed,
                    order.getTotalCost()
            );
        });
    }

}
