package com.loyalbridge.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO for admin information in responses
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminInfoResponse {
    
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // Constructors
    public AdminInfoResponse() {}

    public AdminInfoResponse(Long id, String email, String firstName, String lastName, 
                           String role, Boolean isActive, LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = isActive;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.fullName = buildFullName();
    }

    private String buildFullName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        return ((firstName != null ? firstName : "") + " " + 
                (lastName != null ? lastName : "")).trim();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName;
        this.fullName = buildFullName();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
        this.fullName = buildFullName();
    }

    public String getFullName() { return fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}