package org.example.shippingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shippingservice.domain.dtos.OrderItemsRequest;
import org.example.shippingservice.domain.dtos.OrderProductResponse;
import org.example.shippingservice.domain.dtos.ProductResponse;
import org.example.shippingservice.domain.entity.OrderItem;
import org.example.shippingservice.domain.entity.OrderItemId;
import org.example.shippingservice.domain.dtos.OrderItemsDTO;
import org.example.shippingservice.repository.OrderItemRepository;
import org.example.shippingservice.response.DTOCollectionResponse;
import org.example.shippingservice.service.OrderItemService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shippings")
@Slf4j
@RequiredArgsConstructor
public class OrderItemsController {
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate; // Dùng để gọi API từ product-service

    @GetMapping
    public ResponseEntity<DTOCollectionResponse<OrderItemsDTO>> findAll() {
        log.info("*** OrderItemDto List, controller; fetch all orderItems *");
        return ResponseEntity.ok(new DTOCollectionResponse<>(this.orderItemService.findAll()));
    }

    @GetMapping("/{orderId}/{productId}")
    public ResponseEntity<OrderItemsDTO> findById(
            @PathVariable("orderId") final String orderId,
            @PathVariable("productId") final String productId) {
        log.info("*** OrderItemDto, resource; fetch orderItem by id *");
        return ResponseEntity.ok(this.orderItemService.findById(
                new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId))));
    }

    @GetMapping("/find")
    public ResponseEntity<OrderItemsDTO> findById(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final OrderItemId orderItemId) {
        log.info("*** OrderItemDto, resource; fetch orderItem by id *");
        return ResponseEntity.ok(this.orderItemService.findById(orderItemId));
    }

    @PostMapping
    public ResponseEntity<OrderItemsDTO> save(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final OrderItemsDTO orderItemDto) {
        log.info("*** OrderItemDto, resource; save orderItem *");
        return ResponseEntity.ok(this.orderItemService.save(orderItemDto));
    }

    @PutMapping
    public ResponseEntity<OrderItemsDTO> update(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final OrderItemsDTO orderItemDto) {
        log.info("*** OrderItemDto, resource; update orderItem *");
        return ResponseEntity.ok(this.orderItemService.update(orderItemDto));
    }

    @DeleteMapping("/{orderId}/{productId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable("orderId") final String orderId,
            @PathVariable("productId") final String productId) {
        log.info("*** Boolean, resource; delete orderItem by id *");
        this.orderItemService.deleteById(new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId)));
        return ResponseEntity.ok(true);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteById(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final OrderItemId orderItemId) {
        log.info("*** Boolean, resource; delete orderItem by id *");
        this.orderItemService.deleteById(orderItemId);
        return ResponseEntity.ok(true);
    }

    // thêm sản phẩm dựa vào oderId
    @PostMapping("/add")
    public ResponseEntity<String> addOrderItems(@RequestBody OrderItemsRequest request) {
        try {
            orderItemService.addOrderItems(request.getOrderId(), request.getOrderItems());
            return ResponseEntity.ok("Order items added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Lấy chi tiết sản phẩm của đơn hàng user
    @GetMapping("/{orderId}/products")
    public ResponseEntity<?> getOrderProducts(@PathVariable Integer orderId) {
        try {
            // 1. Lấy danh sách orderItems từ orderId
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
            if (orderItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm nào cho đơn hàng: " + orderId);
            }

            // 2. Lấy danh sách productId từ orderItems
            List<Integer> productIds = orderItems.stream()
                    .map(OrderItem::getProductId)
                    .collect(Collectors.toList());

            // 3. Gửi danh sách productId đến product-service để lấy thông tin chi tiết
            String productServiceUrl = "http://localhost:9056/product-service/api/products/details";
            // Sử dụng ParameterizedTypeReference để ánh xạ danh sách trả về
            ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(
                    productServiceUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(productIds),
                    new ParameterizedTypeReference<List<ProductResponse>>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy thông tin sản phẩm từ Product Service.");
            }

            List<ProductResponse> productDetails = response.getBody();

            // 4. Ghép dữ liệu từ orderItems và productDetails
            List<OrderProductResponse> orderProductResponses = orderItems.stream().map(orderItem -> {
                ProductResponse productDetail = productDetails.stream()
                        .filter(product -> product.getProductId().equals(orderItem.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + orderItem.getProductId()));
                return new OrderProductResponse(
                        productDetail.getProductId(),
                        productDetail.getProductName(),
                        productDetail.getImageUrl(),
                        productDetail.getPrice(),
                        orderItem.getOrderedQuantity(),
                        productDetail.getPrice() * orderItem.getOrderedQuantity()
                );
            }).collect(Collectors.toList());

            // 5. Trả về kết quả
            return ResponseEntity.ok(orderProductResponses);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy thông tin sản phẩm cho đơn hàng: " + e.getMessage());
        }
    }
}

