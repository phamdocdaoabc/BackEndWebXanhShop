package org.example.shippingservice.domain.dtos.adminStatistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatisticDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String categoryName;
    private int soldQuantity;
    private double revenue;
    private double averagePrice;
    private double minPrice;
    private double maxPrice;

    public CategoryStatisticDTO(Integer productId) {
        this.productId = productId;
        this.soldQuantity = 0;
        this.revenue = 0;
        this.minPrice = Double.MAX_VALUE;
        this.maxPrice = 0;
    }

    public CategoryStatisticDTO(String categoryName) {
        this.categoryName = categoryName;
        this.soldQuantity = 0;
        this.revenue = 0;
        this.averagePrice = 0;
        this.minPrice = Double.MAX_VALUE;
        this.maxPrice = 0;
    }
}
