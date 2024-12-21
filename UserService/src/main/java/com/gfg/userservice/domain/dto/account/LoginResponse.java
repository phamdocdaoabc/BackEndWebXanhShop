package com.gfg.userservice.domain.dto.account;

public class LoginResponse {
    private String userName;
    private String token;
    private String role;

    public LoginResponse(String userName, String token, String role) {
        this.userName = userName;
        this.token = token;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
