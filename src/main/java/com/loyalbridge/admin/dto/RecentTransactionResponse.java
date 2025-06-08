package com.loyalbridge.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for recent transaction data
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class RecentTransactionResponse {
    
    private Long id;
    private String userName;
    private String partnerName;
    private String type;
    private BigDecimal pointsAmount;
    private BigDecimal convertedAmount;
    private String status;
    private LocalDateTime timestamp;

    // Constructors
    public RecentTransactionResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getPointsAmount() { return pointsAmount; }
    public void setPointsAmount(BigDecimal pointsAmount) { this.pointsAmount = pointsAmount; }

    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}