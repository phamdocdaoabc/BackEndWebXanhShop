package org.example.productservice.repository;

import org.example.productservice.domain.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImages, Integer> {
    List<ProductImages> findByProduct_ProductId(Integer productId);


}
