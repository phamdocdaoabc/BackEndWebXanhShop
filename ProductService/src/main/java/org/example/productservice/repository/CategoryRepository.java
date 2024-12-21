package org.example.productservice.repository;

import org.example.productservice.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    @Query("SELECT c.categoryId, c.categoryName, COUNT(p.productId) " +
            "FROM Category c LEFT JOIN Product p ON c.categoryId = p.category.categoryId " +
            "GROUP BY c.categoryId, c.categoryName")
    List<Object[]> countProductsByCategory();

    Optional<Category> findByCategoryName(String categoryName);

}
