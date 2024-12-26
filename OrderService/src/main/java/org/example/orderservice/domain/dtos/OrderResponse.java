package org.example.orderservice.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.example.orderservice.domain.enums.OderStatusEnum;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate orderDate;
    private String address;
    private String phone;
    private OderStatusEnum status;
    private Boolean isPayed;
    private Double totalCost;

    public OrderResponse(Integer orderId, LocalDate orderDate, String address, String phone, OderStatusEnum status, Boolean isPayed, Double totalCost) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.address = address;
        this.phone = phone;
        this.status = status;
        this.isPayed = isPayed;
        this.totalCost = totalCost;
    }
}
