package com.loyalbridge.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for partner response data
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartnerResponse {
    
    private Long id;
    private String name;
    private String description;
    private String apiUrl;
    private String authMethod;
    private BigDecimal conversionRate;
    private Boolean isActive;
    private String webhookUrl;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private Long totalTransactions;
    private BigDecimal totalAmountProcessed;
    private LocalDateTime lastTransactionAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PartnerResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public Integer getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(Integer connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public Integer getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Integer readTimeout) { this.readTimeout = readTimeout; }

    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

    public BigDecimal getTotalAmountProcessed() { return totalAmountProcessed; }
    public void setTotalAmountProcessed(BigDecimal totalAmountProcessed) { this.totalAmountProcessed = totalAmountProcessed; }

    public LocalDateTime getLastTransactionAt() { return lastTransactionAt; }
    public void setLastTransactionAt(LocalDateTime lastTransactionAt) { this.lastTransactionAt = lastTransactionAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}