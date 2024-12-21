package org.example.productservice.mapper;

import org.example.productservice.domain.entity.Category;
import org.example.productservice.domain.entity.Product;
import org.example.productservice.domain.dto.CategoryDTO;
import org.example.productservice.domain.dto.ProductDTO;

public interface ProductMapping {

    public static ProductDTO map(final Product product) {
        return ProductDTO.builder().
                productId(product.getProductId())
                .productName(product.getProductName())
                .sku(product.getSku())
                .price(product.getPrice())
                .quantity(product.getQuantily())
                .description(product.getDescription())
                .discount(product.getDiscount())
                .productType((product.getProductType()))
                .categoryDto(CategoryDTO.builder()
                        .categoryId(product.getCategory().getCategoryId())
                        .categoryName(product.getCategory().getCategoryName())
                        .categoryImageUrl(product.getCategory().getCategoryImageUrl())
                        .build())

                .build();
    }

    public static Product map(final ProductDTO productDTO) {
        return Product.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .sku(productDTO.getSku())
                .price(productDTO.getPrice())
                .quantily(productDTO.getQuantity())
                .description(productDTO.getDescription())
                .discount(productDTO.getDiscount())
                .productType(productDTO.getProductType())
                .category(Category.builder()
                        .categoryId(productDTO.getCategoryDto().getCategoryId())
                        .categoryName(productDTO.getCategoryDto().getCategoryName())
                        .categoryImageUrl(productDTO.getCategoryDto().getCategoryImageUrl())
                        .build())


                .build();
    }
}
