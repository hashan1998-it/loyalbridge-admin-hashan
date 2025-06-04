package com.loyalbridge.admin.service;

import com.loyalbridge.admin.model.Admin;
import com.loyalbridge.admin.model.AdminRole;
import com.loyalbridge.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for initializing default data
 * 
 * Creates default admin users for development and testing
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializationService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmins();
    }

    /**
     * Create default admin users if they don't exist
     */
    private void createDefaultAdmins() {
        logger.info("Initializing default admin users...");

        // Super Admin
        createAdminIfNotExists(
            "admin@loyalbridge.io",
            "AdminPassword123!",
            AdminRole.SUPER_ADMIN,
            "Super",
            "Admin"
        );

        // Finance Team
        createAdminIfNotExists(
            "finance@loyalbridge.io", 
            "FinancePassword123!",
            AdminRole.FINANCE_TEAM,
            "Finance",
            "Manager"
        );

        // Support Staff
        createAdminIfNotExists(
            "support@loyalbridge.io",
            "SupportPassword123!",
            AdminRole.SUPPORT_STAFF,
            "Support",
            "Agent"
        );

        // Partner Admin
        createAdminIfNotExists(
            "partner@loyalbridge.io",
            "PartnerPassword123!",
            AdminRole.PARTNER_ADMIN,
            "Partner",
            "Manager"
        );

        logger.info("Default admin users initialization completed");
    }

    /**
     * Create admin user if it doesn't exist
     */
    private void createAdminIfNotExists(String email, String password, AdminRole role, 
                                       String firstName, String lastName) {
        if (!adminRepository.existsByEmail(email)) {
            Admin admin = new Admin();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(role);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setIsActive(true);
            
            adminRepository.save(admin);
            logger.info("Created default admin: {} with role: {}", email, role);
        } else {
            logger.debug("Admin already exists: {}", email);
        }
    }
}