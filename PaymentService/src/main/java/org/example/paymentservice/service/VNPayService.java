package org.example.paymentservice.service;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    String createOrder(int total, String orderInfor, Integer orderId);

    int orderReturn(HttpServletRequest request);
}
