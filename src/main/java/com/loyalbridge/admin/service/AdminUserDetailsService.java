package com.loyalbridge.admin.service;

import com.loyalbridge.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService for Spring Security
 * 
 * Loads admin user details for authentication
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
public class AdminUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserDetailsService.class);
    
    private final AdminRepository adminRepository;

    public AdminUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * Load user by username (email) for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user details for email: {}", email);
        
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Admin not found with email: {}", email);
                    return new UsernameNotFoundException("Admin not found with email: " + email);
                });
    }
}