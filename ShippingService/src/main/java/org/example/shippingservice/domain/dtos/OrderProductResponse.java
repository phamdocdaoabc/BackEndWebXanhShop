package org.example.shippingservice.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
@Data
@Builder
public class OrderProductResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String name;
    private String imageUrl;
    private Double price; // Giá ban đầu
    private Integer quantity; // Số lượng
    private Double totalPrice; // Giá sau khi nhân số lượng

    public OrderProductResponse(Integer productId, String name, String imageUrl, Double price, Integer quantity, Double totalPrice) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

}
