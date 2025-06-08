package com.loyalbridge.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for updating partner information
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UpdatePartnerRequest {
    
    private String name;
    private String description;
    
    @Pattern(regexp = "^https?://.*", message = "API URL must be a valid HTTP/HTTPS URL")
    private String apiUrl;

    @Pattern(regexp = "API_KEY|OAUTH2|BASIC_AUTH|JWT_TOKEN", 
             message = "Auth method must be API_KEY, OAUTH2, BASIC_AUTH, or JWT_TOKEN")
    private String authMethod;

    @Positive(message = "Conversion rate must be positive")
    private BigDecimal conversionRate;

    private String webhookUrl;
    private Integer connectionTimeout;
    private Integer readTimeout;

    // Constructors
    public UpdatePartnerRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getAuthMethod() { return authMethod; }
    public void setAuthMethod(String authMethod) { this.authMethod = authMethod; }

    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public Integer getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(Integer connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public Integer getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Integer readTimeout) { this.readTimeout = readTimeout; }
}