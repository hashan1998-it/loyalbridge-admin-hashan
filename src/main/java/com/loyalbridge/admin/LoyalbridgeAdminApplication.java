package com.loyalbridge.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for LoyalBridge Admin Panel
 * 
 * A comprehensive admin panel for managing loyalty program users,
 * partners, conversions, and analytics.
 * 
 * Features:
 * - JWT-based authentication with RBAC
 * - User management with advanced filtering
 * - Partner integration management
 * - Real-time dashboard analytics
 * - Conversion tracking and reporting
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 * @since 2024-06-04
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class LoyalBridgeAdminApplication {

    /**
     * Main entry point for the LoyalBridge Admin Panel application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(LoyalBridgeAdminApplication.class, args);
    }
}