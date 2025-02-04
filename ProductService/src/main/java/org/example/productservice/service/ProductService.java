package org.example.productservice.service;

import org.example.productservice.domain.dto.ProductDTO;
import org.example.productservice.domain.dto.ProductDetailDTO;
import org.example.productservice.domain.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    ProductDTO findById(final Integer productId);
    ProductDTO save(final ProductDTO productDTO);
    ProductDTO update(final ProductDTO productDTO);

    void deleteById(final Integer productId);

    // Lấy danh sách sản phẩm ALL
    List<ProductDTO> getProductsWithImage();

    // Lấy danh sách sản phẩm theo product type
    List<ProductDTO> getNewProductsWithImage(String productType);

    // lấy chi tiết sản phẩn dựa vào id
    ProductDetailDTO getProductDetail(Integer productId);

    // Kiểm tra hết hàng
    int getProductQuantity(Integer productId);

    // Lấy danh sách sản phẩm theo categoryID
    List<ProductDTO> getProductsWithCategoryId(Integer categoryId);

    // Lấy danh sách sản phẩm ALL (có phân trang)
    Page<ProductDTO> getProductsWithPagination(int page, int size);

    // Lấy danh sách sản phẩm dựa vào categoryID (có phâm trang)
    Page<ProductDTO> getProductsWithCategoryIdPage(Integer categoryId, int page, int size);

    // lấy danh sách sản phẩm dựa vào danh sách id sản phẩm
    List<ProductResponse> getProductDetails(List<Integer> productIds);
}
