package org.example.productservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.productservice.domain.entity.Product;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer productId;
    private String productName;
    private String sku;
    private Double price;
    private Integer quantity;
    private float discount;
    private String description;
    private String productType;
    private List<String> imageUrl; // URL của ảnh đại diện
    private String categoryName;

    public static ProductDetailDTO fromEntity(Product product, List<String> imageUrls, String categoryId) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantily());
        dto.setDiscount(product.getDiscount());
        dto.setProductType(product.getProductType());
        dto.setDescription(product.getDescription());
        dto.setImageUrl(imageUrls);
        dto.setCategoryName(categoryId);
        return dto;
    }
}
