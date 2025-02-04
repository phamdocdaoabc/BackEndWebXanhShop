package org.example.productservice.domain.dto.ProductAdmin;

import java.util.List;

public class ProductImageRequest {
    private List<String> imageUrls;

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
