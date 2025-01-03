package org.example.productservice.service;

import org.example.productservice.domain.dto.CategoryDTO;
import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<CategoryDTO> findAll();
    CategoryDTO findById(final Integer categoryId);
    CategoryDTO save(final CategoryDTO categoryDTO);
    CategoryDTO update(final CategoryDTO categoryDTO);
    CategoryDTO update(final Integer categoryId,final CategoryDTO categoryDTO);
    void deleteById(final Integer categoryId);
    // list thể loại và count sản phẩm
    List<Map<String, Object>> countProductsByCategory();
}
