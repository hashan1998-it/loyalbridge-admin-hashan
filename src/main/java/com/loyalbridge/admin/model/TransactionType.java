package com.loyalbridge.admin.model;

/**
 * Enumeration of transaction types
 * 
 * @author LoyalBridge Development Team
 */
public enum TransactionType {
    /**
     * Points earned by user
     */
    EARN,
    
    /**
     * Points redeemed by user
     */
    REDEEM,
    
    /**
     * Points transferred between accounts
     */
    TRANSFER,
    
    /**
     * Manual adjustment by admin
     */
    ADJUSTMENT,
    
    /**
     * Bonus points awarded
     */
    BONUS,
    
    /**
     * Points expired
     */
    EXPIRY
}