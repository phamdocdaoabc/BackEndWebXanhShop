package org.example.orderservice.domain.dtos.OrderAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderQuarterlyStatisticDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int quarter;
    private int year;
    private long totalOrders;
    private double totalRevenue;
    private double averagePrice;
    private double minPrice;
    private double maxPrice;
}
