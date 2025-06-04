package com.loyalbridge.admin.model;

/**
 * Enumeration of user account statuses
 * 
 * @author LoyalBridge Development Team
 */
public enum UserStatus {
    /**
     * Active user - can earn and redeem points
     */
    ACTIVE,
    
    /**
     * Frozen user - temporarily disabled, cannot perform transactions
     */
    FROZEN,
    
    /**
     * Suspended user - disciplinary action, limited access
     */
    SUSPENDED,
    
    /**
     * Inactive user - account deactivated by user or system
     */
    INACTIVE
}