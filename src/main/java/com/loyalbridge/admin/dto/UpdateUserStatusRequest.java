package com.loyalbridge.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for updating user status
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UpdateUserStatusRequest {
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "ACTIVE|FROZEN|SUSPENDED|INACTIVE", 
             message = "Status must be ACTIVE, FROZEN, SUSPENDED, or INACTIVE")
    private String status;

    private String reason;

    // Constructors
    public UpdateUserStatusRequest() {}

    public UpdateUserStatusRequest(String status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "UpdateUserStatusRequest{" +
                "status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}