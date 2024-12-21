package org.example.productservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    public ProductResponse(Integer productId, String productName, String imageUrl, Double price, float discount) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
    }

}
