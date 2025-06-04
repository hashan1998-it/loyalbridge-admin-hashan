package com.loyalbridge.admin.model;

/**
 * Enumeration of conversion transaction statuses
 * 
 * @author LoyalBridge Development Team
 */
public enum ConversionStatus {
    /**
     * Transaction created but not yet processed
     */
    PENDING,
    
    /**
     * Transaction is being processed
     */
    PROCESSING,
    
    /**
     * Transaction completed successfully
     */
    COMPLETED,
    
    /**
     * Transaction failed due to error
     */
    FAILED,
    
    /**
     * Transaction cancelled by user or system
     */
    CANCELLED
}