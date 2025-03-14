package org.example.orderservice.domain.dtos.OrderAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String fullName;
    private int soldQuantity;
    private double revenue;
    private double averagePrice;
    private double minPrice;
    private double maxPrice;

    public OrderStatisticsDTO(Integer userId) {
        this.userId = userId;
        this.soldQuantity = 0;
        this.revenue = 0;
        this.averagePrice = 0;
        this.minPrice = Double.MAX_VALUE;
        this.maxPrice = 0;
    }

}
