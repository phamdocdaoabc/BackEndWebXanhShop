package org.example.productservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.domain.dto.ProductAdmin.ProductRequest;
import org.example.productservice.domain.dto.ProductDTO;
import org.example.productservice.domain.dto.ProductDetailDTO;
import org.example.productservice.domain.dto.ProductResponse;
import org.example.productservice.domain.entity.Category;
import org.example.productservice.domain.entity.Product;
import org.example.productservice.domain.entity.ProductImages;
import org.example.productservice.domain.response.DTOCollectionResponse;
import org.example.productservice.repository.CategoryRepository;
import org.example.productservice.repository.ProductImageRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private final ProductService productService;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final ProductImageRepository productImageRepository;

    @PostMapping
    public ResponseEntity<ProductDTO> save(
            @RequestBody
            @NotNull(message = "Input must be not null")
            @Valid final ProductDTO productDTO
    ) {
        log.info("ProductDTo, Controller; save the products");
        return ResponseEntity.ok(this.productService.save(productDTO));
    }

    // Thêm sản phẩm
    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest) {
        // Kiểm tra xem category có tồn tại không
        Optional<Category> category = categoryRepository.findByCategoryName(productRequest.getCategoryName());
        if (!category.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Tạo mới sản phẩm
        Product product = new Product();
        product.setProductName(productRequest.getProductName());
        product.setCategory(category.get());
        product.setPrice(productRequest.getPrice());
        product.setQuantily(productRequest.getQuantity());
        product.setDescription(productRequest.getDescription());
        product.setDiscount(productRequest.getDiscount());
        product.setCreatedAt(productRequest.getCreateAt());
        product.setProductType(productRequest.getProductType());

        // Lưu thông tin sản phẩm mới vào cơ sở dữ liệu
        Product savedProduct = productRepository.save(product);

        return ResponseEntity.ok(savedProduct);
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

    // Update product admin
    @PutMapping("/updateProduct/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable Integer productId, @RequestBody ProductRequest productRequest) {
        // Lấy thông tin sản phẩm hiện tại từ DB
        Optional<Product> existingProduct = productRepository.findById(productId);

        if (!existingProduct.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Product product = existingProduct.get();

        // Lấy category_id từ bảng Categories
        Optional<Category> category = categoryRepository.findByCategoryName(productRequest.getCategoryName());
        if (!category.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Cập nhật thông tin sản phẩm
        product.setProductName(productRequest.getProductName());
        product.setCategory(category.get());
        product.setPrice(productRequest.getPrice());
        product.setQuantily(productRequest.getQuantity());
        product.setDescription(productRequest.getDescription());
        product.setDiscount(productRequest.getDiscount());
        product.setCreatedAt(productRequest.getCreateAt());
        product.setProductType(productRequest.getProductType());

        // Lưu lại thông tin sản phẩm đã cập nhật
        productRepository.save(product);

        return ResponseEntity.ok("Product updated successfully");
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

    // API Update ảnh product
    @Value("${upload.dir}")
    private String uploadDir;
    @PutMapping("/{productId}/images")
    public ResponseEntity<List<ProductImages>> updateProductImage(
            @PathVariable Integer productId,
            @RequestParam("images") List<MultipartFile> images) {

        // Kiểm tra danh sách ảnh có trống không
        if (images == null || images.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Kiểm tra sản phẩm có tồn tại không
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Xoá ảnh cũ trong bảng Product_Images
        List<ProductImages> productImagesList = productImageRepository.findByProduct_ProductId(productId);
        if (productImagesList != null && !productImagesList.isEmpty()) {
            productImageRepository.deleteAll(productImagesList);  // Xóa tất cả các bản ghi trong danh sách
        }


        // Tạo danh sách ảnh đã lưu
        List<ProductImages> newImages = new ArrayList<>();

        // Lưu ảnh vào thư mục "static/images/products"
        for (MultipartFile image : images) {
            try {
                // Tạo đường dẫn lưu ảnh
                String fileName = image.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);
                System.out.println("Upload directory: " + uploadDir);
                System.out.println("File path: " + path.toString());


                // Lưu file vào thư mục
                Files.createDirectories(path.getParent());  // Tạo thư mục nếu chưa tồn tại
                image.transferTo(path);

                // Tạo URL của ảnh vừa lưu
                String imageUrl = "http://localhost:9056/product-service/" + fileName;

                // Lưu thông tin ảnh vào cơ sở dữ liệu
                ProductImages productImage = new ProductImages();
                productImage.setProduct(product.get());
                productImage.setProductImageUrl(imageUrl);
                newImages.add(productImage);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        // Lưu ảnh vào database
        productImageRepository.saveAll(newImages);
        return ResponseEntity.ok(newImages);
    }
}