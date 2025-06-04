package com.loyalbridge.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Partner entity representing loyalty program integration partners
 * 
 * Supports various authentication methods and conversion rates
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "partners", indexes = {
    @Index(name = "idx_partner_name", columnList = "name"),
    @Index(name = "idx_partner_active", columnList = "is_active"),
    @Index(name = "idx_partner_created_at", columnList = "created_at")
})
public class Partner {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Partner name is required")
    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "api_url")
    private String apiUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method")
    private AuthMethod authMethod;

    @NotNull(message = "Conversion rate is required")
    @Positive(message = "Conversion rate must be positive")
    @Column(name = "conversion_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal conversionRate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "connection_timeout")
    private Integer connectionTimeout = 30000; // 30 seconds

    @Column(name = "read_timeout")
    private Integer readTimeout = 60000; // 60 seconds

    @Column(name = "total_transactions")
    private Long totalTransactions = 0L;

    @Column(name = "total_amount_processed", precision = 12, scale = 2)
    private BigDecimal totalAmountProcessed = BigDecimal.ZERO;

    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Partner() {}

    public Partner(String name, String apiUrl, AuthMethod authMethod, BigDecimal conversionRate) {
        this.name = name;
        this.apiUrl = apiUrl;
        this.authMethod = authMethod;
        this.conversionRate = conversionRate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public void recordTransaction(BigDecimal amount) {
        this.totalTransactions++;
        this.totalAmountProcessed = this.totalAmountProcessed.add(amount);
        this.lastTransactionAt = LocalDateTime.now();
    }

    public BigDecimal calculateConvertedAmount(BigDecimal points) {
        return points.multiply(conversionRate);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public AuthMethod getAuthMethod() { return authMethod; }
    public void setAuthMethod(AuthMethod authMethod) { this.authMethod = authMethod; }

    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

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

    @Override
    public String toString() {
        return "Partner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", authMethod=" + authMethod +
                ", conversionRate=" + conversionRate +
                ", isActive=" + isActive +
                '}';
    }
}