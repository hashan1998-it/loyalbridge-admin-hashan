package com.loyalbridge.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for dashboard overview data
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {
    
    private UserStatsResponse userStats;
    private PartnerStatsResponse partnerStats;
    private ConversionStatsResponse conversionStats;
    private List<RecentTransactionResponse> recentTransactions;
    private List<ConversionTrendResponse> conversionTrends;
    private SystemHealthResponse systemHealth;
    private LocalDateTime lastUpdated;

    // Constructors
    public DashboardResponse() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public UserStatsResponse getUserStats() { return userStats; }
    public void setUserStats(UserStatsResponse userStats) { this.userStats = userStats; }

    public PartnerStatsResponse getPartnerStats() { return partnerStats; }
    public void setPartnerStats(PartnerStatsResponse partnerStats) { this.partnerStats = partnerStats; }

    public ConversionStatsResponse getConversionStats() { return conversionStats; }
    public void setConversionStats(ConversionStatsResponse conversionStats) { this.conversionStats = conversionStats; }

    public List<RecentTransactionResponse> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<RecentTransactionResponse> recentTransactions) { this.recentTransactions = recentTransactions; }

    public List<ConversionTrendResponse> getConversionTrends() { return conversionTrends; }
    public void setConversionTrends(List<ConversionTrendResponse> conversionTrends) { this.conversionTrends = conversionTrends; }

    public SystemHealthResponse getSystemHealth() { return systemHealth; }
    public void setSystemHealth(SystemHealthResponse systemHealth) { this.systemHealth = systemHealth; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}