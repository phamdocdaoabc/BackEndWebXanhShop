package org.example.orderservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.audit.BaseEntity;
import org.example.orderservice.constant.AppConstant;
import org.example.orderservice.domain.enums.OderStatusEnum;
import org.example.orderservice.domain.enums.PaymentMethodEnum;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"cart"})
public class Order extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", unique = true, nullable = false, updatable = false)
    private Integer orderId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = AppConstant.LOCAL_DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    @DateTimeFormat(pattern = AppConstant.LOCAL_DATE_FORMAT)
    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "order_desc")
    private String orderDesc;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "phone")
    private String phone;

    @Column (name = "order_note")
    private String orderNote;

    @Enumerated(EnumType.STRING) // Lưu giá trị Enum dưới dạng String trong DB (PENDING, CONFIRMED, v.v.)
    @Column(name = "order_status", nullable = false)
    private OderStatusEnum orderStatus;

    @Enumerated(EnumType.STRING) // Lưu giá trị Enum dưới dạng String trong DB (COD, ONLINE)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethodEnum paymentMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id" , referencedColumnName = "cart_id")
    private Cart cart;

}
