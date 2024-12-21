package org.example.orderservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OderStatusEnum {
    PENDING,       // Đơn hàng đang chờ xử lý
    CONFIRMED,     // Đơn hàng đã được xác nhận
    SHIPPED,       // Đơn hàng đang vận chuyển
    DELIVERED,     // Đơn hàng đã được giao
    CANCELLED;     // Đơn hàng đã bị hủy

    /**
     * Phương thức để lấy mô tả thân thiện cho trạng thái đơn hàng.
     */
    public String getFriendlyName() {
        switch (this) {
            case PENDING:
                return "Chờ xử lý";
            case CONFIRMED:
                return "Đã xác nhận";
            case SHIPPED:
                return "Đang giao";
            case DELIVERED:
                return "Đã giao";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
}
