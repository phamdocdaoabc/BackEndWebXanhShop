package org.example.shippingservice.audit;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.shippingservice.constant.AppConstant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@EntityListeners(AuditingEntityListener.class) // Cần để kích hoạt @CreatedDate, @LastModifiedDate
abstract public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.LOCAL_DATE_FORMAT)
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.LOCAL_DATE_FORMAT)
    @Column(name = "updated_at")
    private LocalDate updatedAt;

}
