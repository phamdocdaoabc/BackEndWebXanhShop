package org.example.orderservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.audit.BaseEntity;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class CartItem extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cart_item_id", nullable = false, updatable = false)
    private Integer cartItemId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private Integer productId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    // Constructors
    public CartItem(Cart cart, Integer productId, Integer quantity, Double price) {
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

}
