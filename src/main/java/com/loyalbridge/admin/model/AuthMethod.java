package com.loyalbridge.admin.model;

/**
 * Enumeration of authentication methods for partner APIs
 * 
 * @author LoyalBridge Development Team
 */
public enum AuthMethod {
    /**
     * API Key authentication
     */
    API_KEY,
    
    /**
     * OAuth 2.0 authentication
     */
    OAUTH2,
    
    /**
     * Basic HTTP authentication
     */
    BASIC_AUTH,
    
    /**
     * JWT token authentication
     */
    JWT_TOKEN
}