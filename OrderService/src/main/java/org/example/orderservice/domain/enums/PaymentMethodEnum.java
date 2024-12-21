package org.example.orderservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethodEnum {
    COD,      // Thanh toán khi nhận hàng (Cash On Delivery)
    ONLINE;   // Thanh toán trực tuyến

    /**
     * Phương thức để lấy mô tả thân thiện cho phương thức thanh toán.
     */
    public String getFriendlyName() {
        switch (this) {
            case COD:
                return "Thanh toán khi nhận hàng (COD)";
            case ONLINE:
                return "Thanh toán trực tuyến";
            default:
                return "Không xác định";
        }
    }
}
