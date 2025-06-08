package com.loyalbridge.admin.dto;

import java.math.BigDecimal;

/**
 * DTO for conversion statistics
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class ConversionStatsResponse {
    
    private Long totalConversions;
    private Long completedConversions;
    private Long pendingConversions;
    private Long failedConversions;
    private BigDecimal totalPointsConverted;
    private BigDecimal totalAmountConverted;
    private BigDecimal conversionSuccessRate;
    private Long todayConversions;
    private Long weeklyConversions;

    // Constructors
    public ConversionStatsResponse() {}

    // Getters and Setters
    public Long getTotalConversions() { return totalConversions; }
    public void setTotalConversions(Long totalConversions) { this.totalConversions = totalConversions; }

    public Long getCompletedConversions() { return completedConversions; }
    public void setCompletedConversions(Long completedConversions) { this.completedConversions = completedConversions; }

    public Long getPendingConversions() { return pendingConversions; }
    public void setPendingConversions(Long pendingConversions) { this.pendingConversions = pendingConversions; }

    public Long getFailedConversions() { return failedConversions; }
    public void setFailedConversions(Long failedConversions) { this.failedConversions = failedConversions; }

    public BigDecimal getTotalPointsConverted() { return totalPointsConverted; }
    public void setTotalPointsConverted(BigDecimal totalPointsConverted) { this.totalPointsConverted = totalPointsConverted; }

    public BigDecimal getTotalAmountConverted() { return totalAmountConverted; }
    public void setTotalAmountConverted(BigDecimal totalAmountConverted) { this.totalAmountConverted = totalAmountConverted; }

    public BigDecimal getConversionSuccessRate() { return conversionSuccessRate; }
    public void setConversionSuccessRate(BigDecimal conversionSuccessRate) { this.conversionSuccessRate = conversionSuccessRate; }

    public Long getTodayConversions() { return todayConversions; }
    public void setTodayConversions(Long todayConversions) { this.todayConversions = todayConversions; }

    public Long getWeeklyConversions() { return weeklyConversions; }
    public void setWeeklyConversions(Long weeklyConversions) { this.weeklyConversions = weeklyConversions; }
}