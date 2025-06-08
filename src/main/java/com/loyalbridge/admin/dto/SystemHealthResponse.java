package com.loyalbridge.admin.dto;

import java.time.LocalDateTime;

/**
 * DTO for system health status
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class SystemHealthResponse {
    
    private String status;
    private String databaseStatus;
    private Long activeUsers;
    private Long activeSessions;
    private String memoryUsage;
    private LocalDateTime uptime;

    // Constructors
    public SystemHealthResponse() {}

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDatabaseStatus() { return databaseStatus; }
    public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }

    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

    public Long getActiveSessions() { return activeSessions; }
    public void setActiveSessions(Long activeSessions) { this.activeSessions = activeSessions; }

    public String getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(String memoryUsage) { this.memoryUsage = memoryUsage; }

    public LocalDateTime getUptime() { return uptime; }
    public void setUptime(LocalDateTime uptime) { this.uptime = uptime; }
}