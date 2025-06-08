package com.loyalbridge.admin.dto;

/**
 * DTO for partner search criteria
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class PartnerSearchCriteria {
    
    private String name;
    private String authMethod;
    private Boolean isActive;

    // Constructors
    public PartnerSearchCriteria() {}

    public PartnerSearchCriteria(String name, String authMethod, Boolean isActive) {
        this.name = name;
        this.authMethod = authMethod;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthMethod() { return authMethod; }
    public void setAuthMethod(String authMethod) { this.authMethod = authMethod; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    @Override
    public String toString() {
        return "PartnerSearchCriteria{" +
                "name='" + name + '\'' +
                ", authMethod='" + authMethod + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}