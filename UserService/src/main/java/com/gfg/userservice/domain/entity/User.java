package com.gfg.userservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gfg.userservice.audit.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"credential"})
@Data
@Builder
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id" , unique = true, nullable = false, updatable = false)
    private Integer userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "sex")
    private String sex;

    @Column(name = "image_url")
    @Lob
    private String imageUrl;

    @Email(message = "*Input must be in Email format!**")
    private String email;
    private String phone;

    @Column(name = "adress")
    private String adress;

    @Column(name = "birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Credential credential;


    public <T> User(String username, String password, Boolean isEnabled, Boolean isAccountNonExpired, Boolean isCredentialsNonExpired, Boolean isAccountNonLocked, List<T> ts) {
        super();
    }
}
