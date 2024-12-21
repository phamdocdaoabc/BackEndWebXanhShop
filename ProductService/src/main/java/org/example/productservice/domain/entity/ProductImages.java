package org.example.productservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.productservice.audit.BaseEntity;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_images")
@EqualsAndHashCode(callSuper = true, exclude = {"product_images"})
@Builder
public class ProductImages extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", unique = true, nullable = false, updatable = false)
    private Integer imageId;

    @Column(name = "image_url")
    private String productImageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;


}
