package com.loyalbridge.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating user risk flag
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UpdateRiskFlagRequest {
    
    @NotNull(message = "High risk flag is required")
    private Boolean isHighRisk;

    private String reason;

    // Constructors
    public UpdateRiskFlagRequest() {}

    public UpdateRiskFlagRequest(Boolean isHighRisk, String reason) {
        this.isHighRisk = isHighRisk;
        this.reason = reason;
    }

    // Getters and Setters
    public Boolean getIsHighRisk() { return isHighRisk; }
    public void setIsHighRisk(Boolean isHighRisk) { this.isHighRisk = isHighRisk; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "UpdateRiskFlagRequest{" +
                "isHighRisk=" + isHighRisk +
                ", reason='" + reason + '\'' +
                '}';
    }
}