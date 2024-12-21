package com.gfg.userservice.domain.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required.")
    private String user;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String password;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    public void setUserName(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
