package org.example.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.productservice.audit.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@EqualsAndHashCode(callSuper = true, exclude = {"category"})
@Builder
public class Product extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", unique = true,nullable = false,updatable = false)
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "sku")
    private String sku;

    @Column(name = "quantily")
    private int quantily;

    @Column(name = "product_price")
    private Double price;

    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount")
    private float discount;

    @Column(name = "product_type")
    private String productType;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

}
