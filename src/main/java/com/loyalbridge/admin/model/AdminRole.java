package com.loyalbridge.admin.model;

/**
 * Enumeration of admin roles with different permission levels
 * 
 * @author LoyalBridge Development Team
 */
public enum AdminRole {
    /**
     * Super Administrator - Full system access
     */
    SUPER_ADMIN,
    
    /**
     * Finance Team - Financial data and reports access
     */
    FINANCE_TEAM,
    
    /**
     * Support Staff - User support and basic operations
     */
    SUPPORT_STAFF,
    
    /**
     * Partner Administrator - Partner management access
     */
    PARTNER_ADMIN
}