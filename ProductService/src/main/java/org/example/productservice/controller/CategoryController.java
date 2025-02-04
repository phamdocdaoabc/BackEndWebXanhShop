package org.example.productservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.domain.dto.CategoryDTO;
import org.example.productservice.domain.response.DTOCollectionResponse;
import org.example.productservice.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<String> createCategory(@RequestBody CategoryDTO categoryDTO) {
        if (categoryService.save(categoryDTO.getCategoryName())) {
            return ResponseEntity.ok("Thêm thể loại thành công.");
        } else {
            return ResponseEntity.badRequest().body("Tên thể loại đã tồn tại.");
        }
    }

    @GetMapping
    public ResponseEntity<DTOCollectionResponse<CategoryDTO>> findAll() {
        return ResponseEntity.ok(new DTOCollectionResponse<>(this.categoryService.findAll()));
    }

    @GetMapping("/{categoryId}") // Corrected the path variable name
    public ResponseEntity<CategoryDTO> findById(
            @PathVariable("categoryId")
            @NotNull(message = "Input must be not NULL")
            @Valid final String categoryId) {
        return ResponseEntity.ok(this.categoryService.findById(Integer.parseInt(categoryId)));
    }


    @PutMapping("/update/{categoryId}")
    public ResponseEntity<?> uppdateCategory(@PathVariable Integer categoryId,@RequestBody CategoryDTO categoryDTO){
        try {
            categoryService.update(categoryId, categoryDTO);
            return ResponseEntity.ok("Thể loại đã được cập nhật.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cập nhật thể loại thất bại.");
        }
    }

    @DeleteMapping("/{categoryId}") // Corrected the path variable name
    public ResponseEntity<Boolean> delete(
            @PathVariable("categoryId")
            @NotNull(message = "Input must be not NULL")
            @Valid String categoryId) {
        this.categoryService.deleteById(Integer.parseInt(categoryId));
        return ResponseEntity.ok(true);
    }

    // list thể loại và count sản phẩm
    @GetMapping("/count")
    public ResponseEntity<List<Map<String, Object>>> countProductsByCategory(){
        List<Map<String, Object>> counts = categoryService.countProductsByCategory();
        return ResponseEntity.ok(counts);
    }

    // list thể loại và count sản phẩm admin
    @GetMapping("/countPage")
    public Page<Map<String, Object>> getCategoryProductCounts(@RequestParam int page,
                                                              @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryName").ascending());

        // Lấy dữ liệu từ service
        Page<Object[]> categoryData = categoryService.getCategoryProductCounts(pageable);

        // Chuyển đổi dữ liệu trả về từ Object[] thành Map
        return categoryData.map(row -> {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("categoryId", row[0]);
            categoryMap.put("categoryName", row[1]);
            categoryMap.put("productCount", row[2]);
            return categoryMap;
        });
    }


}
