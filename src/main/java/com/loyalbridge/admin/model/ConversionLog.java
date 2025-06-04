package com.loyalbridge.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ConversionLog entity tracking point conversion transactions
 * 
 * Records all point conversions between users and partners
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "conversion_logs", indexes = {
    @Index(name = "idx_conversion_user_id", columnList = "user_id"),
    @Index(name = "idx_conversion_partner_id", columnList = "partner_id"),
    @Index(name = "idx_conversion_status", columnList = "status"),
    @Index(name = "idx_conversion_created_at", columnList = "created_at"),
    @Index(name = "idx_conversion_type", columnList = "transaction_type")
})
public class ConversionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Partner ID is required")
    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull(message = "Points amount is required")
    @Positive(message = "Points amount must be positive")
    @Column(name = "points_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal pointsAmount;

    @Column(name = "converted_amount", precision = 12, scale = 2)
    private BigDecimal convertedAmount;

    @Column(name = "conversion_rate", precision = 10, scale = 4)
    private BigDecimal conversionRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversionStatus status = ConversionStatus.PENDING;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "description")
    private String description;

    @Column(name = "partner_transaction_id")
    private String partnerTransactionId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ConversionLog() {}

    public ConversionLog(Long userId, Long partnerId, TransactionType transactionType, 
                        BigDecimal pointsAmount, BigDecimal convertedAmount) {
        this.userId = userId;
        this.partnerId = partnerId;
        this.transactionType = transactionType;
        this.pointsAmount = pointsAmount;
        this.convertedAmount = convertedAmount;
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
    public void markCompleted() {
        this.status = ConversionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = ConversionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return ConversionStatus.COMPLETED.equals(this.status);
    }

    public boolean isFailed() {
        return ConversionStatus.FAILED.equals(this.status);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPartnerId() { return partnerId; }
    public void setPartnerId(Long partnerId) { this.partnerId = partnerId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getPointsAmount() { return pointsAmount; }
    public void setPointsAmount(BigDecimal pointsAmount) { this.pointsAmount = pointsAmount; }

    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }

    public BigDecimal getConversionRate() { return conversionRate; }
    public void setConversionRate(BigDecimal conversionRate) { this.conversionRate = conversionRate; }

    public ConversionStatus getStatus() { return status; }
    public void setStatus(ConversionStatus status) { this.status = status; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPartnerTransactionId() { return partnerTransactionId; }
    public void setPartnerTransactionId(String partnerTransactionId) { this.partnerTransactionId = partnerTransactionId; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ConversionLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", partnerId=" + partnerId +
                ", transactionType=" + transactionType +
                ", pointsAmount=" + pointsAmount +
                ", convertedAmount=" + convertedAmount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}