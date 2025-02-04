package org.example.orderservice.domain.dtos.OrderAdmin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAdminDTO {
    private Integer orderId;
    private String customerName;
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    private String shippingAddress;
    private Double totalCost;
    private String paymentStatus;
    private String orderStatus;
}
