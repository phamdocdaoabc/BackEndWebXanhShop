package org.example.paymentservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.dtos.OnlinePaymentRequest;
import org.example.paymentservice.dtos.PaymentDTO;
import org.example.paymentservice.dtos.PaymentRequest;
import org.example.paymentservice.model.Payment;
import org.example.paymentservice.repository.PaymentRepository;
import org.example.paymentservice.response.DTOCollectionResponse;
import org.example.paymentservice.service.PaymentService;
import org.example.paymentservice.service.VNPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final PaymentRepository paymentRepository;

    @GetMapping("/test")
    public String test() {
        return "Service is running";
    }

    @GetMapping
    public ResponseEntity<DTOCollectionResponse<PaymentDTO>> findAll() {
        log.info("*** PaymentDto List, controller; fetch all payments *");
        return ResponseEntity.ok(new DTOCollectionResponse<>(this.paymentService.findAll()));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> findById(
            @PathVariable("paymentId")
            @NotBlank(message = "Input must not be blank")
            @Valid final String paymentId) {
        log.info("*** PaymentDto, resource; fetch payment by id *");
        return ResponseEntity.ok(this.paymentService.findById(Integer.parseInt(paymentId)));
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> save(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final PaymentDTO paymentDto) {
        log.info("*** PaymentDto, resource; save payment *");
        return ResponseEntity.ok(this.paymentService.save(paymentDto));
    }

    @PutMapping
    public ResponseEntity<PaymentDTO> update(
            @RequestBody
            @NotNull(message = "Input must not be NULL")
            @Valid final PaymentDTO paymentDto) {
        log.info("*** PaymentDto, resource; update payment *");
        return ResponseEntity.ok(this.paymentService.update(paymentDto));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("paymentId") final String paymentId) {
        log.info("*** Boolean, resource; delete payment by id *");
        this.paymentService.deleteById(Integer.parseInt(paymentId));
        return ResponseEntity.ok(true);
    }

    @PostMapping("/createCod")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setIsPayed(false); // COD chưa thanh toán
        paymentRepository.save(payment);

        return ResponseEntity.ok("Payment created successfully");
    }
    @Autowired
    private  final VNPayService vnPayService;

    // API VNPAY
    @PostMapping("/createOnline")
    public ResponseEntity<?> submitOrder(@RequestBody OnlinePaymentRequest paymentRequest, HttpServletRequest request) {
        try {
            // Lấy base URL từ request
            //String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            // Gọi service để tạo URL thanh toán VNPAY
            String vnpayUrl = vnPayService.createOrder(paymentRequest.getAmount().intValue(),
                    paymentRequest.getDescription(),
                    paymentRequest.getOrderId());

            // Trả về URL thanh toán để redirect
            return ResponseEntity.ok(vnpayUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý đơn hàng: " + e.getMessage());
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    // API xác thực đơn thanh toán
    @GetMapping("/vnpay-payment")
    public void handlePaymentReturn(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Lấy thông tin từ URL
            String orderInfo = URLDecoder.decode(request.getParameter("vnp_OrderInfo"), StandardCharsets.UTF_8);
            String paymentTime = request.getParameter("vnp_PayDate");
            String transactionId = request.getParameter("vnp_TransactionNo");
            String totalPrice = request.getParameter("vnp_Amount");

            // Ghi log để kiểm tra thông tin giao dịch
            logger.info("VNPAY Callback received: OrderInfo={}, PaymentTime={}, TransactionId={}, TotalPrice={}",
                    orderInfo, paymentTime, transactionId, totalPrice);

            // Xử lý trạng thái thanh toán
            int paymentStatus = vnPayService.orderReturn(request);

            if (paymentStatus == 1) {
                // Thanh toán thành công, chuyển hướng tới trang thành công
                logger.info("Payment succeeded for OrderInfo={}", orderInfo);
                response.sendRedirect("http://localhost:5050/templates/web/checkout_VNPAY_success.html");
            } else {
                // Thanh toán thất bại, chuyển hướng tới trang thất bại
                logger.warn("Payment failed for OrderInfo={}", orderInfo);
                response.sendRedirect("http://localhost:5050/templates/web/checkout_VNPAY_failed.html");
            }
        } catch (Exception e) {
            // Log lỗi và chuyển hướng đến trang lỗi chung nếu xảy ra lỗi
            logger.error("Error occurred while handling VNPAY callback: ", e);
            try {
                response.sendRedirect("/checkout_VNPAY_failed.html");
            } catch (IOException ioException) {
                logger.error("Error redirecting to failed page: ", ioException);
            }
        }
    }

    // API lấy isPayed dựa vào orderId
    @GetMapping("/{orderId}/status")
    public Boolean getPaymentStatus(@PathVariable Integer orderId) {
        return paymentService.isPaymentCompleted(orderId);
    }
}
