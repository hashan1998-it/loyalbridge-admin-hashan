package com.loyalbridge.admin.dto;

/**
 * DTO for token responses
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class TokenResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    // Constructors
    public TokenResponse() {}

    public TokenResponse(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}