package com.gfg.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String fullName;
    private String sex;
    private String imageUrl;
    private String email;
    private String phone;
    private String adress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;
    /*
    public void setBirthday(Long timestamp) {
        if (timestamp != null) {
            this.birthday = new Date(timestamp);
        }
    }*/

    // Getter và Setter (nếu cần)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    @JsonProperty("credential")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private CredentialDTO credentialDTO;
}
