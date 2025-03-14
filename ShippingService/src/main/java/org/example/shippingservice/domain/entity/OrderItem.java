package org.example.shippingservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.shippingservice.audit.BaseEntity;

import java.io.Serializable;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class OrderItem extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private Integer productId;

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private Integer orderId;

    @Column(name = "order_quantity")
    private Integer orderedQuantity;

    @Column(name = "price")
    private Double price;

}
