package com.loyalbridge.admin.dto;

import java.math.BigDecimal;

/**
 * DTO for user statistics response
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UserStatsResponse {
    
    private Long totalUsers;
    private Long activeUsers;
    private Long frozenUsers;
    private Long suspendedUsers;
    private Long inactiveUsers;
    private Long verifiedUsers;
    private Long highRiskUsers;
    private BigDecimal totalPointsInSystem;
    private BigDecimal totalLifetimeEarnings;
    private BigDecimal totalLifetimeRedemptions;

    // Constructors
    public UserStatsResponse() {}

    // Getters and Setters
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

    public Long getFrozenUsers() { return frozenUsers; }
    public void setFrozenUsers(Long frozenUsers) { this.frozenUsers = frozenUsers; }

    public Long getSuspendedUsers() { return suspendedUsers; }
    public void setSuspendedUsers(Long suspendedUsers) { this.suspendedUsers = suspendedUsers; }

    public Long getInactiveUsers() { return inactiveUsers; }
    public void setInactiveUsers(Long inactiveUsers) { this.inactiveUsers = inactiveUsers; }

    public Long getVerifiedUsers() { return verifiedUsers; }
    public void setVerifiedUsers(Long verifiedUsers) { this.verifiedUsers = verifiedUsers; }

    public Long getHighRiskUsers() { return highRiskUsers; }
    public void setHighRiskUsers(Long highRiskUsers) { this.highRiskUsers = highRiskUsers; }

    public BigDecimal getTotalPointsInSystem() { return totalPointsInSystem; }
    public void setTotalPointsInSystem(BigDecimal totalPointsInSystem) { this.totalPointsInSystem = totalPointsInSystem; }

    public BigDecimal getTotalLifetimeEarnings() { return totalLifetimeEarnings; }
    public void setTotalLifetimeEarnings(BigDecimal totalLifetimeEarnings) { this.totalLifetimeEarnings = totalLifetimeEarnings; }

    public BigDecimal getTotalLifetimeRedemptions() { return totalLifetimeRedemptions; }
    public void setTotalLifetimeRedemptions(BigDecimal totalLifetimeRedemptions) { this.totalLifetimeRedemptions = totalLifetimeRedemptions; }
}