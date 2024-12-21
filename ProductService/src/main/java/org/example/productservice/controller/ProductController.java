package org.example.productservice.controller;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.domain.dto.ProductDTO;
import org.example.productservice.domain.dto.ProductDetailDTO;
import org.example.productservice.domain.dto.ProductResponse;
import org.example.productservice.domain.entity.Category;
import org.example.productservice.domain.response.DTOCollectionResponse;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {


    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<ProductDTO> save(
            @RequestBody
            @NotNull(message = "Input must be not null")
            @Valid final ProductDTO productDTO
    ) {
        log.info("ProductDTo, Controller; save the products");
        return ResponseEntity.ok(this.productService.save(productDTO));

    }

    @GetMapping
    public ResponseEntity<DTOCollectionResponse<ProductDTO>> findAll() {
        log.info("ProductDTO, Controller, fetch all the products");
        return ResponseEntity.ok(new DTOCollectionResponse<>(this.productService.findAll()));
    }


    @PutMapping
    public ResponseEntity<ProductDTO> update(
            @RequestBody
            @NotNull(message = "Input must be not null")
            @Valid ProductDTO productDTO) {
        return ResponseEntity.ok(this.productService.update(productDTO));
    }

    @PutMapping("/productId")
    public ResponseEntity<ProductDTO> update(
            @PathVariable("productId")
            @RequestBody
            @NotNull(message = "Input must be not null")
            @Valid final String productId,
            @RequestBody
            @NotNull(message = "Input must be not null")
            @Valid ProductDTO productDTO) {
        return ResponseEntity.ok(this.productService.update(Integer.parseInt(productId), productDTO));
    }

    @DeleteMapping("/productId")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable("productId") final String productId) {
        this.productService.deleteById(Integer.parseInt(productId));
        return ResponseEntity.ok(true);
    }

    // API: Lấy danh sách sản phẩm all (kèm ảnh đại diện)
    @GetMapping("/listProduct")
    public ResponseEntity<List<ProductDTO>> getProducts() {
        List<ProductDTO> products = productService.getProductsWithImage();
        return ResponseEntity.ok(products);
    }

    // API: Lấy danh sách sản phẩm new (kèm ảnh đại diện)
    @GetMapping("/listNew")
    public ResponseEntity<List<ProductDTO>> getNewProducts() {
        List<ProductDTO> products = productService.getNewProductsWithImage("new");
        return ResponseEntity.ok(products);
    }
    // API: Lấy danh sách sản phẩm đặc sắc (kèm ảnh đại diện)
    @GetMapping("/listFeatured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts() {
        List<ProductDTO> products = productService.getNewProductsWithImage("featured");
        return ResponseEntity.ok(products);
    }
    // API: Lấy danh sách sản phẩm bán chạy (kèm ảnh đại diện)
    @GetMapping("/listBestseller")
    public ResponseEntity<List<ProductDTO>> getBestsellerProducts() {
        List<ProductDTO> products = productService.getNewProductsWithImage("bestseller");
        return ResponseEntity.ok(products);
    }
    // API: Lấy chi tiết sản phẩm (kèm danh sách ảnh chi tiết)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Integer id) {
        ProductDetailDTO product = productService.getProductDetail(id);
        return ResponseEntity.ok(product);
    }
    // API kiểm tra sản phẩm còn hàng không
    @GetMapping("/{id}/quantity")
    public ResponseEntity<Map<String, Object>> checkProductQuantity(@PathVariable Integer id){
        int quantity = productService.getProductQuantity(id);
        Map<String, Object> response = new HashMap<>();
        response.put("productId", id);
        response.put("quantity", quantity);
        response.put("inStock", quantity > 0);
        return ResponseEntity.ok(response);
    }

    // API: Lấy danh sách sản phẩm theo tên thể loại (kèm ảnh đại diện)
    @GetMapping("/by-categoryName")
    public ResponseEntity<?> listProductByCategoryName(@RequestParam(required = false) String categoryName) {
        try {
            // Tìm categoryId theo categoryName
            Integer categoryId = null;
            if (categoryName != null && !categoryName.isEmpty()) {
                categoryId = categoryRepository.findByCategoryName(categoryName)
                        .map(Category::getCategoryId) // Lấy categoryId từ entity Category
                        .orElse(null);
            }

            // Kiểm tra nếu categoryId bị thiếu hoặc không hợp lệ
            if (categoryId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("categoryId không được để trống hoặc không hợp lệ.");
            }

            // Lấy danh sách sản phẩm theo categoryId
            List<ProductDTO> products = productService.getProductsWithCategoryId(categoryId);
            return ResponseEntity.ok(products);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
    }

    // API: Lấy danh sách sản phẩm theo Id thể loại (kèm ảnh đại diện)
    @GetMapping("/by-categoryId")
    public ResponseEntity<?> listProductByCategoryId(@RequestParam(required = false) Integer categoryId) {
        try {
            // Kiểm tra nếu categoryId bị thiếu hoặc không hợp lệ
            if (categoryId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("categoryId không được để trống");
            }

            List<ProductDTO> products = productService.getProductsWithCategoryId(categoryId);
            return ResponseEntity.ok(products);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
        }
    }


    // Lấy tất cả sản phẩm (phân trang)
    @GetMapping("/listProductPage")
    public ResponseEntity<Page<ProductDTO>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<ProductDTO> products = productService.getProductsWithPagination(page, size);
        return ResponseEntity.ok(products);
    }

    // Lấy sản phẩm theo thể loại (phân trang)
    @GetMapping("/by-category-page")
    public ResponseEntity<Page<ProductDTO>> listProductByCategoryPage(
            @RequestParam Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductDTO> products = productService.getProductsWithCategoryIdPage(categoryId, page, size);
        return ResponseEntity.ok(products);
    }

    // API lấy thông tin sản phẩm theo danh sách productId
    @PostMapping("/details")
    public ResponseEntity<?> getProductDetails(@RequestBody List<Integer> productIds) {
        try {
            List<ProductResponse> productDetails = productService.getProductDetails(productIds);
            return ResponseEntity.ok(productDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
        }
    }
}