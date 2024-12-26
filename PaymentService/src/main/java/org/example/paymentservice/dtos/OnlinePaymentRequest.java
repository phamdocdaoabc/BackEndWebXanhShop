package org.example.paymentservice.dtos;

import lombok.Data;

@Data
public class OnlinePaymentRequest {
    private Integer orderId; // Mã đơn hàng
    private Double amount;   // Tổng tiền thanh toán
    private String description;  // Thông tin mô tả đơn hàng

    // Getters và Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
