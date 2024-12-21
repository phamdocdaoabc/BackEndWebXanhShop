package com.gfg.userservice.domain.dto.account;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    String userName;

    @NotBlank
    String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
