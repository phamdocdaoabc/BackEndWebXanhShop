package org.example.productservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.domain.entity.Category;
import org.example.productservice.domain.dto.CategoryDTO;
import org.example.productservice.exception.CategoryNotFoundException;
import org.example.productservice.mapper.CategoryMapping;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;



import static org.example.productservice.mapper.CategoryMapping.convertToDTO;
import static org.example.productservice.mapper.CategoryMapping.convertToEntity;

@Service
@Transactional
@Slf4j

public class CategoryServiceImplementation implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImplementation(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> findAll() {
        log.info("CategoryDTO, Fetch all the categories");
        return this.categoryRepository.findAll()
                .stream()
                .map(CategoryMapping::map)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public CategoryDTO findById(Integer categoryId) {
        log.info("CategoryDTO, Fetch the categories by using categoryId");
        return this.categoryRepository.findById(categoryId)
                .map(CategoryMapping::map).
                orElseThrow(()->new CategoryNotFoundException(String.format("Category with id %d is not found",categoryId)));

    }

    public boolean save(String categoryName) {
        // Kiểm tra nếu tên thể loại đã tồn tại
        if (categoryRepository.existsByCategoryName(categoryName)) {
            return false;
        }
        // Tạo mới thể loại và lưu vào cơ sở dữ liệu
        Category category = new Category();
        category.setCategoryName(categoryName);
        categoryRepository.save(category);
        return true;
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO) {
        log.info("CategoryDTO, update the category");
        return CategoryMapping.map(this.categoryRepository.save(CategoryMapping.map(categoryDTO)));
    }

    // cập nhật tên thể loại
    @Override
    public void update(Integer categoryId, CategoryDTO categoryDTO) {
       Category category = categoryRepository.findById(categoryId)
               .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

       category.setCategoryName(categoryDTO.getCategoryName());
       categoryRepository.save(category);
    }

    @Override
    public void deleteById(Integer categoryId) {
        log.info("CategoryDTO, delete the category");
        this.categoryRepository.deleteById(categoryId);
    }

    // list thể loại và count sản phẩm
    @Override
    public List<Map<String, Object>> countProductsByCategory() {
        List<Object[]> result = categoryRepository.countProductsByCategory();
        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : result) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", row[0]);
            item.put("categoryName", row[1]);
            item.put("productCount", row[2]);
            response.add(item);
        }
        return response;
    }

    // list thể loại và count sản phẩm admin có phân trang
    @Override
    public Page<Object[]> getCategoryProductCounts(Pageable pageable) {
        return categoryRepository.countProductsByCategory(pageable);
    }

}
