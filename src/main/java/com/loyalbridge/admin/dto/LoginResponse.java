package com.loyalbridge.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO for admin login responses
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private AdminInfoResponse admin;
    private boolean requires2FA;
    private String otpMessage;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, long expiresIn, 
                        AdminInfoResponse admin) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.admin = admin;
        this.requires2FA = false;
    }

    public LoginResponse(AdminInfoResponse admin, String otpMessage) {
        this.admin = admin;
        this.requires2FA = true;
        this.otpMessage = otpMessage;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public AdminInfoResponse getAdmin() { return admin; }
    public void setAdmin(AdminInfoResponse admin) { this.admin = admin; }

    public boolean isRequires2FA() { return requires2FA; }
    public void setRequires2FA(boolean requires2FA) { this.requires2FA = requires2FA; }

    public String getOtpMessage() { return otpMessage; }
    public void setOtpMessage(String otpMessage) { this.otpMessage = otpMessage; }
}