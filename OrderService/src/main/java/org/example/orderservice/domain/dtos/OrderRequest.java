package org.example.orderservice.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer userId;
    private UserInfo userInfo;
    private String paymentMethod;
    private double totalCost;
    @Data
    public static class UserInfo {
        private String name;
        private String address;
        private String phone;
        private String note;
    }
}
