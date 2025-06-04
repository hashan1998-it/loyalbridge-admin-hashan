package com.loyalbridge.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for 2FA verification requests
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class TwoFactorRequest {
    
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    private String otp;

    // Constructors
    public TwoFactorRequest() {}

    public TwoFactorRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    @Override
    public String toString() {
        return "TwoFactorRequest{" +
                "email='" + email + '\'' +
                ", otp='[PROTECTED]'" +
                '}';
    }
}