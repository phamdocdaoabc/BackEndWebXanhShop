package org.example.productservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

//@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private float discount;
    private String categoryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    public ProductResponse(Integer productId, String productName, String imageUrl, Double price, float discount, String categoryName, LocalDate createdAt) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

}
