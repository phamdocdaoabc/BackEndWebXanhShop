package com.gfg.userservice.domain.dto;

public class UserFullNameDTO {
    private Integer userId;
    private String fullName;

    // Constructor
    public UserFullNameDTO(Integer userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
