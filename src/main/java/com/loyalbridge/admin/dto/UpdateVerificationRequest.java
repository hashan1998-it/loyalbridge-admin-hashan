package com.loyalbridge.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating user verification status
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UpdateVerificationRequest {
    
    @NotNull(message = "Verification status is required")
    private Boolean isVerified;

    private String notes;

    // Constructors
    public UpdateVerificationRequest() {}

    public UpdateVerificationRequest(Boolean isVerified, String notes) {
        this.isVerified = isVerified;
        this.notes = notes;
    }

    // Getters and Setters
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "UpdateVerificationRequest{" +
                "isVerified=" + isVerified +
                ", notes='" + notes + '\'' +
                '}';
    }
}