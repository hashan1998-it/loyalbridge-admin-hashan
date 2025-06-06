package com.loyalbridge.admin.dto;

/**
 * DTO for detailed user response with additional information
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class UserDetailResponse extends UserResponse {
    
    private Integer totalTransactions;
    private String riskReason;
    private String verificationNotes;

    // Constructors
    public UserDetailResponse() {}

    // Getters and Setters
    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }

    public String getRiskReason() { return riskReason; }
    public void setRiskReason(String riskReason) { this.riskReason = riskReason; }

    public String getVerificationNotes() { return verificationNotes; }
    public void setVerificationNotes(String verificationNotes) { this.verificationNotes = verificationNotes; }
}