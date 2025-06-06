package com.loyalbridge.admin.dto;

/**
 * DTO for user search criteria
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UserSearchCriteria {

    private String name;
    private String phone;
    private String status;
    private Boolean isHighRisk;
    private Boolean isVerified;

    // Constructors
    public UserSearchCriteria() {
    }

    public UserSearchCriteria(String name, String phone, String status,
            Boolean isHighRisk, Boolean isVerified) {
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.isHighRisk = isHighRisk;
        this.isVerified = isVerified;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsHighRisk() {
        return isHighRisk;
    }

    public void setIsHighRisk(Boolean isHighRisk) {
        this.isHighRisk = isHighRisk;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    @Override
    public String toString() {
        return "UserSearchCriteria{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", isHighRisk=" + isHighRisk +
                ", isVerified=" + isVerified +
                '}';
    }
}