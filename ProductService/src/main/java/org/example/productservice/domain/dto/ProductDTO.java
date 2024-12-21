package org.example.productservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.productservice.domain.entity.Product;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productName;
    private String sku;
    private Double price;
    private Integer quantity;
    private float discount;
    private String description;
    private String productType;
    private String imageUrl; // URL của ảnh đại diện

    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantily());
        dto.setDiscount(product.getDiscount());
        dto.setDescription(product.getDescription());
        dto.setProductType(product.getProductType());
        return dto;
    }

    @JsonProperty("category")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CategoryDTO categoryDto;

}
