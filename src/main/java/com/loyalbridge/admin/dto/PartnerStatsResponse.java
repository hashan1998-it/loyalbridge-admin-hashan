package com.loyalbridge.admin.dto;

import java.math.BigDecimal;

/**
 * DTO for partner statistics
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class PartnerStatsResponse {
    
    private Long totalPartners;
    private Long activePartners;
    private Long inactivePartners;
    private BigDecimal totalAmountProcessed;
    private Long totalTransactions;
    private String topPartnerByTransactions;
    private String topPartnerByAmount;

    // Constructors
    public PartnerStatsResponse() {}

    // Getters and Setters
    public Long getTotalPartners() { return totalPartners; }
    public void setTotalPartners(Long totalPartners) { this.totalPartners = totalPartners; }

    public Long getActivePartners() { return activePartners; }
    public void setActivePartners(Long activePartners) { this.activePartners = activePartners; }

    public Long getInactivePartners() { return inactivePartners; }
    public void setInactivePartners(Long inactivePartners) { this.inactivePartners = inactivePartners; }

    public BigDecimal getTotalAmountProcessed() { return totalAmountProcessed; }
    public void setTotalAmountProcessed(BigDecimal totalAmountProcessed) { this.totalAmountProcessed = totalAmountProcessed; }

    public Long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Long totalTransactions) { this.totalTransactions = totalTransactions; }

    public String getTopPartnerByTransactions() { return topPartnerByTransactions; }
    public void setTopPartnerByTransactions(String topPartnerByTransactions) { this.topPartnerByTransactions = topPartnerByTransactions; }

    public String getTopPartnerByAmount() { return topPartnerByAmount; }
    public void setTopPartnerByAmount(String topPartnerByAmount) { this.topPartnerByAmount = topPartnerByAmount; }
}