package org.example.productservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productservice.domain.dto.ProductDTO;
import org.example.productservice.domain.dto.ProductDetailDTO;
import org.example.productservice.domain.dto.ProductResponse;
import org.example.productservice.domain.entity.Product;
import org.example.productservice.domain.entity.ProductImages;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.ProductMapping;
import org.example.productservice.repository.ProductImageRepository;
import org.example.productservice.repository.ProductRepository;
import org.example.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;
    @Override
    public List<ProductDTO> findAll() {
        log.info("ProductDTO, Fetch all the products");
        return this.productRepository.findAll()
                .stream().map(ProductMapping::map).distinct().collect(Collectors.toList());
    }

    @Override
    public ProductDTO findById(Integer productId) {
        log.info("ProductDTO, Fetch the products using productId");
        return this.productRepository.findById(productId)
                .map(ProductMapping::map)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id: %d not found", productId)));

    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        log.info("ProductDTO, save the products");
        return ProductMapping.map(this.productRepository.save(ProductMapping.map(productDTO)));
    }

    @Override
    public ProductDTO update(ProductDTO productDTO) {
        log.info("ProductDTO, update the products");
        return ProductMapping.map(
                this.productRepository.save(ProductMapping.map(productDTO))
        );
    }

    @Override
    public void deleteById(Integer productId) {
        log.info("ProductDTO, update the products");
        this.productRepository.deleteById(productId);

    }

    // Lấy danh sách sản phẩm all
    @Override
    public List<ProductDTO> getProductsWithImage() {
        List<Product> listProducts = productRepository.findAll();
        return listProducts.stream().map(product -> {
            ProductDTO dto = ProductDTO.fromEntity(product);
            // Tìm danh sách ảnh của sản phẩm và lấy ảnh đầu tiên
            List<ProductImages> images = productImageRepository.findByProduct_ProductId(product.getProductId());

            if (!images.isEmpty()) {
                // Lấy ảnh đầu tiên trong danh sách ảnh của sản phẩm
                dto.setImageUrl(images.get(0).getProductImageUrl());
            } else {
                // Nếu sản phẩm không có ảnh nào, gán URL ảnh mặc định
                dto.setImageUrl("/images/no-image.png");
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // Lấy danh sách sản phẩm theo productType(new-featured-bestseller)
    @Override
    public List<ProductDTO> getNewProductsWithImage(String productType) {
        List<Product> newProducts = productRepository.findByProductType(productType);
        return newProducts.stream().map(product -> {
            ProductDTO dto = ProductDTO.fromEntity(product);
            // Tìm danh sách ảnh của sản phẩm và lấy ảnh đầu tiên
            List<ProductImages> images = productImageRepository.findByProduct_ProductId(product.getProductId());

            if (!images.isEmpty()) {
                // Lấy ảnh đầu tiên trong danh sách ảnh của sản phẩm
                dto.setImageUrl(images.get(0).getProductImageUrl());
            } else {
                // Nếu sản phẩm không có ảnh nào, gán URL ảnh mặc định
                dto.setImageUrl("/images/no-image.png");
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // Lấy chi tiết sản phẩm kèm danh sách ảnh
    @Override
    public ProductDetailDTO getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        List<String> imageUrls = productImageRepository.findByProduct_ProductId(productId)
                .stream().map(ProductImages::getProductImageUrl)
                .collect(Collectors.toList());

        String categoryName = product.getCategory().getCategoryName();

        return ProductDetailDTO.fromEntity(product, imageUrls, categoryName);
    }

    // Kiểm tra sản phẩm hết hàng
    @Override
    public int getProductQuantity(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        return product.getQuantily();
    }

    // lấy list sản phẩm với categoryId
    @Override
    public List<ProductDTO> getProductsWithCategoryId(Integer categoryId) {
        // Kiểm tra nếu categoryId bị null
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId không được để trống");
        }
        List<Product> newProducts = productRepository.findProductsByCategoryId(categoryId);
        return newProducts.stream().map(product -> {
            ProductDTO dto = ProductDTO.fromEntity(product);
            // Tìm danh sách ảnh của sản phẩm và lấy ảnh đầu tiên
            List<ProductImages> images = productImageRepository.findByProduct_ProductId(product.getProductId());

            if (!images.isEmpty()) {
                // Lấy ảnh đầu tiên trong danh sách ảnh của sản phẩm
                dto.setImageUrl(images.get(0).getProductImageUrl());
            } else {
                // Nếu sản phẩm không có ảnh nào, gán URL ảnh mặc định
                dto.setImageUrl("/images/no-image.png");
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // lấy list sản phẩm ALL (có phân trang)
    @Override
    public Page<ProductDTO> getProductsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(product -> {
            ProductDTO dto = ProductDTO.fromEntity(product);
            List<ProductImages> images = productImageRepository.findByProduct_ProductId(product.getProductId());
            dto.setImageUrl(!images.isEmpty() ? images.get(0).getProductImageUrl() : "/images/no-image.png");
            return dto;
        });
    }

    // lấy list sản phẩm với categoryId (có phân trang)
    @Override
    public Page<ProductDTO> getProductsWithCategoryIdPage(Integer categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findByCategory_CategoryId(categoryId, pageable);
        return products.map(product -> {
            ProductDTO dto = ProductDTO.fromEntity(product);
            List<ProductImages> images = productImageRepository.findByProduct_ProductId(product.getProductId());
            dto.setImageUrl(!images.isEmpty() ? images.get(0).getProductImageUrl() : "/images/no-image.png");
            return dto;
        });
    }

    // lấy list product dựa vào list id product
    @Override
    public List<ProductResponse> getProductDetails(List<Integer> productIds) {
        List<Object[]> productData = productRepository.findProductsWithImages(productIds);

        // Tạo danh sách ProductResponse từ dữ liệu trả về
        Map<Integer, ProductResponse> productResponseMap = new HashMap<>();

        for (Object[] row : productData) {
            Product product = (Product) row[0];
            List<ProductImages> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());

            // Kiểm tra nếu sản phẩm có ít nhất một ảnh
            String imageUrl = null;
            if (!productImages.isEmpty()) {
                imageUrl = productImages.get(0).getProductImageUrl(); // Lấy ảnh đầu tiên trong danh sách
            }

            if (!productResponseMap.containsKey(product.getProductId())) {
                productResponseMap.put(product.getProductId(), new ProductResponse(
                        product.getProductId(),
                        product.getProductName(),
                        imageUrl, // Đường dẫn ảnh
                        product.getPrice(),
                        product.getDiscount(),
                        product.getCategory().getCategoryName(),
                        product.getCreatedAt()
                ));
            }
        }

        return new ArrayList<>(productResponseMap.values());
    }
}
