package org.example.productservice.service;

import org.example.productservice.domain.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<CategoryDTO> findAll();
    CategoryDTO findById(final Integer categoryId);
    boolean save(String categoryName);
    CategoryDTO update(final CategoryDTO categoryDTO);
    void update(final Integer categoryId,final CategoryDTO categoryDTO);
    void deleteById(final Integer categoryId);
    // list thể loại và count sản phẩm
    List<Map<String, Object>> countProductsByCategory();

    Page<Object[]> getCategoryProductCounts(Pageable pageable);
}
