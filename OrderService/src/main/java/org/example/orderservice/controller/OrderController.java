package org.example.orderservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    // api lấy thông tin đơn hàng của ngời dùng
    /*@GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUserId(@PathVariable Integer userId) {
        return orderService.getOrderDetailsByUserId(userId);
    }*/
    @GetMapping("/user")
    public Page<OrderResponse> getOrderDetailsByUserId(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return orderService.getOrderDetailsByUserId(userId, page, size);
    }
}
