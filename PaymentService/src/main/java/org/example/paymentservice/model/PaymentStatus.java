package org.example.paymentservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    PENDING,    // Đang xử lý
    COMPLETED,  // Hoàn tất
    FAILED;      // Thất bại

}
