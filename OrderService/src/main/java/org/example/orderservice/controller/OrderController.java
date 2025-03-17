package org.example.orderservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.domain.dtos.OrderAdmin.*;
import org.example.orderservice.domain.dtos.OrderDTO;
import org.example.orderservice.domain.dtos.OrderRequest;
import org.example.orderservice.domain.dtos.OrderResponse;
import org.example.orderservice.domain.entity.Cart;
import org.example.orderservice.domain.entity.Order;
import org.example.orderservice.domain.enums.OderStatusEnum;
import org.example.orderservice.domain.enums.PaymentMethodEnum;
import org.example.orderservice.domain.response.ResponseDtoCollection;
import org.example.orderservice.repository.CartRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private final OrderService orderService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<OrderDTO> save(@RequestBody
                               @NotNull(message = "Input must be not null")
                               @Valid OrderDTO orderDTO) {
        log.info("OrderController, Save the OrderDTO");
        return ResponseEntity.ok(this.orderService.save(orderDTO));
    }

    @PutMapping
    public  ResponseEntity<OrderDTO> update(@RequestBody
                                            @NotNull(message = "Input must be not null")
                                            @Valid OrderDTO orderDTO) {
        log.info("OrderController, Update the OrderDTO");
        return ResponseEntity.ok(this.orderService.save(orderDTO));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> update(@PathVariable("orderId")
                                           @NotBlank(message = "Input must be not null")
                                           @Valid String orderId,
                                           @RequestBody
                                           @NotNull(message = "Input must be not null")
                                           @Valid OrderDTO orderDTO) {
        log.info("OrderController, Update the OrderDTO with OrderId");
        return ResponseEntity.ok(this.orderService.update(Integer.parseInt(orderId),orderDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseDtoCollection<OrderDTO>> findAll() {
        log.info("OrderController, Retrieve the OrderDTOs");
        return ResponseEntity.ok(new ResponseDtoCollection<>(this.orderService.findAll()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> findById(@PathVariable("orderId")
                                             @NotBlank(message = "Input must be not null")
                                             @Valid String orderId) {
        log.info("OrderController, Retrieve the OrderDTOs using the orderId");
        return ResponseEntity.ok(this.orderService.findById(Integer.parseInt(orderId)));
    }

    @DeleteMapping("/orderId")
    public ResponseEntity<Boolean> delete(@PathVariable("orderId")
                                          @NotBlank(message = "Input must be not null")
                                          @Valid String orderId) {
        log.info("OrderController, Delete the OrderDTO");
        this.orderService.deleteById(Integer.parseInt(orderId));
        return ResponseEntity.ok(true);
    }

    // API Tại oder dựa vào userId
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // Lấy userId từ request
            Integer userId = orderRequest.getUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().body("UserId không được để trống");
            }

            // Tìm cartId phù hợp với userId
            Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại!"));
            if (cart == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy giỏ hàng cho userId: " + userId);
            }
            // Tạo mới đơn hàng
            Order newOrder = new Order();
            newOrder.setCart(cart);
            newOrder.setOrderDate(LocalDate.now());
            newOrder.setOrderDesc("Đơn hàng của người dùng");
            newOrder.setTotalCost(orderRequest.getTotalCost()); //tổng giá trị giỏ hàng
            newOrder.setShippingAddress(orderRequest.getUserInfo().getAddress());
            newOrder.setPhone(orderRequest.getUserInfo().getPhone());
            newOrder.setOrderNote(orderRequest.getUserInfo().getNote());
            newOrder.setOrderStatus(OderStatusEnum.PENDING); // Trạng thái ban đầu là PENDING
            newOrder.setPaymentMethod(PaymentMethodEnum.valueOf(orderRequest.getPaymentMethod().toUpperCase()));

            // Lưu đơn hàng vào cơ sở dữ liệu
            Order savedOrder = orderRepository.save(newOrder);

            // Trả về orderId vừa tạo
            return ResponseEntity.ok(savedOrder.getOrderId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    // api lấy thông tin đơn hàng của người dùng
    @GetMapping("/user")
    public Page<OrderResponse> getOrderDetailsByUserId(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return orderService.getOrderDetailsByUserId(userId, page, size);
    }

    // API Hủy đơn hàng
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Boolean> cancelOrder(@PathVariable Integer orderId){
        Order order = orderRepository.findByOrderId(orderId);
        // nếu còn ở trạng thái chờ xử lý thì hủy được
        if(order.getOrderStatus() == OderStatusEnum.PENDING){
            order.setOrderStatus(OderStatusEnum.CANCELLED);
            orderRepository.save(order);
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.ok(false);
        }
    }

    // APi Thống kê admin
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingOrders", orderService.getPendingOrderCount());
        stats.put("totalRevenue", orderService.getTotalRevenue());
        stats.put("deliveredOrders", orderService.getDeliveredOrderCount());
        stats.put("cancelledOrders", orderService.getCancelledOrderCount());
        return ResponseEntity.ok(stats);
    }

    // API lấy Thông tin đơn hàng admin
    @GetMapping("order-admin")
    public  ResponseEntity<Page<OrderAdminDTO>> getAllOrders( @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){

        // Cấu hình phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách đơn hàng theo trang
        Page<Order> ordersPage = orderService.getAllOrders(pageable);

        // Chuyển đổi từng Order sang OrderAdminDTO
        Page<OrderAdminDTO> responses = ordersPage.map(order -> {
            // Gọi API UserService để lấy thông tin người dùng
            String userUrl = "http://UserService/user-service/api/users/fullName/" + order.getCart().getUserId();
            UserFullNameDTO user = restTemplate.getForObject(userUrl, UserFullNameDTO.class);

            // Gọi API PaymentService để lấy trạng thái thanh toán
            String paymentUrl = "http://PaymentService/payment-service/api/payments/" + order.getOrderId() + "/status";
            Boolean isPayed = restTemplate.getForObject(paymentUrl, Boolean.class);

            // Tạo response
            return new OrderAdminDTO(
                    order.getOrderId(),
                    user != null ? user.getFullName() : "N/A", // Kiểm tra null
                    order.getPhone(),
                    order.getOrderDate(),
                    order.getShippingAddress(),
                    order.getTotalCost(),
                    isPayed != null && isPayed ? "Đã thanh toán" : "Chưa thanh toán",
                    order.getOrderStatus().toString()
            );
        });
        return ResponseEntity.ok(responses);
    }

    // UPDATE orderStatus (ADMIN)
    @PutMapping("/update/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody OrderStatusDTO orderStatusDTO) {
        try {
            orderService.updateOrderStatus(orderId, orderStatusDTO.getOrderStatus());
            return ResponseEntity.ok("Trạng thái đơn hàng đã được cập nhật.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cập nhật trạng thái thất bại.");
        }
    }

    // Admin user statistcs
    @GetMapping("/user_statistics")
    public ResponseEntity<Page<OrderStatisticsDTO>> getUserStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<OrderStatisticsDTO> orderStatisticDTOS = orderService.getOrderStatistics(startDate, endDate, pageable);
        return ResponseEntity.ok(orderStatisticDTOS);

    }

    // Admin year statistcs
    @GetMapping("/year_statistics")
    public ResponseEntity<Page<OrderYearlyStatisticDTO>> getOrderYearStatistics(
            @RequestParam("year") int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OrderYearlyStatisticDTO> statistics = orderService.getOrderYearStatistics(year, pageable);
        return ResponseEntity.ok(statistics);
    }
    // Admin month statistcs
    @GetMapping("/month_statistics")
    public ResponseEntity<Page<OrderMonthlyStatisticDTO>> getOrderMonthStatistics(
            @RequestParam("year") int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ){
       Pageable pageable = PageRequest.of(page, size);
       Page<OrderMonthlyStatisticDTO> statisrics = orderService.getOrderMonthStatistics(year, pageable);
       return ResponseEntity.ok(statisrics);
    }

    //Admin quarter statistics.
    @GetMapping("/quarter_statistics")
    public ResponseEntity<Page<OrderQuarterlyStatisticDTO>> getOrderQuarterStatistics(
            @RequestParam("year") int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderQuarterlyStatisticDTO> statisrics = orderService.getOrderStatisticsByQuarter(year, pageable);
        return ResponseEntity.ok(statisrics);
    }

}
