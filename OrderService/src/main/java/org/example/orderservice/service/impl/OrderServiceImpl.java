package org.example.orderservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderMonthlyStatisticDTO;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderStatisticsDTO;
import org.example.orderservice.domain.dtos.OrderAdmin.OrderYearlyStatisticDTO;
import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.Order;
import org.example.orderservice.domain.enums.OderStatusEnum;
import org.example.orderservice.exception.CartNotFoundException;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mappers.OrderMapping;
import org.example.orderservice.repository.CartRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
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
            String paymentUrl = "http://PaymentService/payment-service/api/payments/" + order.getOrderId() + "/status";
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
    // số đơn hàng mới
    @Override
    public long getPendingOrderCount() {
        return orderRepository.countPendingOrders();
    }
    // Tổng doanh thu
    @Override
    public Double getTotalRevenue() {
        return orderRepository.calculateTotalRevenue();
    }
    // Số đơn hàng thành công
    @Override
    public long getDeliveredOrderCount() {
        return orderRepository.countDeliveredOrders();
    }
    // Số đơn hàng hủy
    @Override
    public long getCancelledOrderCount() {
        return orderRepository.countCancelledOrders();
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public void updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findByOrderId(orderId);
        order.setOrderStatus(OderStatusEnum.valueOf(newStatus));
        orderRepository.save(order);
    }

    // Call UserService to get fullName
    private Map<Integer, String> fetchUserNames(Set<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String url = "http://UserService/user-service/api/users/full_name?userIds=" + String.join(",", userIds.stream().map(String::valueOf).toArray(String[]::new));
        try {
            ResponseEntity<Map<Integer, String>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<Integer, String>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            return Collections.emptyMap(); // Nếu lỗi thì trả về danh sách rỗng để tránh ảnh hưởng hệ thống
        }
    }
    // Admin User Statistics
    @Override
    public Page<OrderStatisticsDTO> getOrderStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Query list OrderItems by date range
        Page<Order> orderPage = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
        List<Order> orders = orderPage.getContent();

        // Group data by category_Name
        Map<Integer, OrderStatisticsDTO> statisticDTOMap = new HashMap<>();
        Set<Integer> userIds = new HashSet<>(); // Tập hợp userId để tránh trùng lặp

        for(Order order : orders){
            Integer userId = order.getCart().getUserId();
            userIds.add(userId); // Thêm userId vào danh sách gọi API

            OrderStatisticsDTO stat = statisticDTOMap.getOrDefault(userId, new OrderStatisticsDTO(userId));

            stat.setSoldQuantity(stat.getSoldQuantity() + 1);
            stat.setRevenue(stat.getRevenue() + order.getTotalCost());

            stat.setMinPrice(Math.min(stat.getMinPrice(), order.getTotalCost()));
            stat.setMaxPrice(Math.max(stat.getMaxPrice(), order.getTotalCost()));

            statisticDTOMap.put(userId, stat);
        }

        // **Gọi API một lần để lấy danh sách tên người dùng**
        Map<Integer, String> userNames = fetchUserNames(userIds);

        // Call api UserService to get fullName
        for(OrderStatisticsDTO stat : statisticDTOMap.values()){
            stat.setFullName(userNames.getOrDefault(stat.getUserId(), "Không xác định!"));
            if(stat.getSoldQuantity() > 0){
                stat.setAveragePrice(stat.getRevenue() / stat.getSoldQuantity()); // Calculate the average value
            }
        }

        // convert list to `Page<CategoryStatisticDTO>`
        List<OrderStatisticsDTO> orderStatisticDTOS = new ArrayList<>(statisticDTOMap.values());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orderStatisticDTOS.size());

        return new PageImpl<>(orderStatisticDTOS.subList(start, end), pageable, orderStatisticDTOS.size());
    }

    // Admin year statistics - order
    @Override
    public Page<OrderYearlyStatisticDTO> getOrderYearStatistics(int year, Pageable pageable) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Order> allOrders = orderRepository.findByOrderDateBetween(startDate, endDate);
        Page<Order> orderPage = orderRepository.findByOrderDateBetween(startDate, endDate, pageable);

        if (orderPage.isEmpty()) {
            return Page.empty();
        }

        long totalOrders = allOrders.size();
        double totalRevenue = allOrders.stream().mapToDouble(Order::getTotalCost).sum();
        double minPrice = allOrders.stream().mapToDouble(Order::getTotalCost).min().orElse(0);
        double maxPrice = allOrders.stream().mapToDouble(Order::getTotalCost).max().orElse(0);
        double averagePrice = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        OrderYearlyStatisticDTO statistics = new OrderYearlyStatisticDTO(year, totalOrders, totalRevenue, averagePrice, minPrice, maxPrice);

        return new PageImpl<>(List.of(statistics), pageable, 1);
    }

    // Admin Month Statistics - order
    @Override
    public Page<OrderMonthlyStatisticDTO> getOrderMonthStatistics(int year, Pageable pageable) {
        List<OrderMonthlyStatisticDTO> statisticsList = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            List<Order> monthlyOrder = orderRepository.findByOrderDateBetween(startDate, endDate);
            long totalOrder = monthlyOrder.size();
            double totalRevenue = monthlyOrder.stream().mapToDouble(Order::getTotalCost).sum();
            double averagePrice = totalOrder > 0 ? totalRevenue / totalOrder: 0;
            double minPrice = monthlyOrder.stream().mapToDouble(Order::getTotalCost).min().orElse(0);
            double maxPrice = monthlyOrder.stream().mapToDouble(Order::getTotalCost).max().orElse(0);

            OrderMonthlyStatisticDTO statisticDTO = new OrderMonthlyStatisticDTO(month, year, totalOrder, totalRevenue, averagePrice, minPrice, maxPrice);
            statisticsList.add(statisticDTO);
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), statisticsList.size());
        List<OrderMonthlyStatisticDTO> pageList = statisticsList.subList(start, end);
        return new PageImpl<>(pageList, pageable, statisticsList.size());
    }

}
