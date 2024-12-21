package org.example.productservice.repository;

import com.linecorp.armeria.server.annotation.Param;
import org.example.productservice.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findByProductType(String productType);

    @Override
    Optional<Product> findById(Integer integer);

    @Query("SELECT p FROM Product p WHERE p.category.categoryId = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Integer categoryId);

    Page<Product> findByCategory_CategoryId(Integer categoryId, Pageable pageable);

    @Query("SELECT p, pi.productImageUrl FROM Product p " +
            "LEFT JOIN ProductImages pi ON p.productId = pi.product.productId " +
            "WHERE p.productId IN :productIds")
    List<Object[]> findProductsWithImages(@Param("productIds") List<Integer> productIds);
}