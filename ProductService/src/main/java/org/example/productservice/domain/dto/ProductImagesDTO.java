package org.example.productservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductImagesDTO {
    private static final long serialVersionUID = 1L;

    private Integer imageId;
    private String productImageUrl;

    @JsonProperty("products")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProductDTO productDTO;

}
