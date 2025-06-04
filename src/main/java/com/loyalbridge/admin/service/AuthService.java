package com.loyalbridge.admin.service;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.model.Admin;
import com.loyalbridge.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Authentication service for admin login, logout, and token management
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // In-memory storage for OTP (use Redis in production)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> otpExpiryStorage = new ConcurrentHashMap<>();
    
    // Blacklisted tokens (use Redis in production)
    private final Map<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();

    public AuthService(AdminRepository adminRepository, 
                      AuthenticationManager authenticationManager,
                      JwtService jwtService, 
                      PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate admin and return login response
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            // Check if admin is active
            if (!admin.getIsActive()) {
                logger.warn("Login attempt for inactive admin: {}", request.getEmail());
                throw new BadCredentialsException("Account is inactive");
            }

            // Update last login
            admin.setLastLogin(LocalDateTime.now());
            admin = adminRepository.save(admin);

            // For demo purposes, randomly require 2FA (20% chance)
            boolean requires2FA = shouldRequire2FA(admin);
            
            if (requires2FA) {
                logger.info("2FA required for admin: {}", request.getEmail());
                return handle2FAFlow(admin);
            }

            logger.info("Login successful for admin: {}", request.getEmail());
            return generateTokenResponse(admin);

        } catch (Exception e) {
            logger.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    /**
     * Handle 2FA flow
     */
    private LoginResponse handle2FAFlow(Admin admin) {
        // Generate and store OTP
        String otp = generateOTP();
        otpStorage.put(admin.getEmail(), otp);
        otpExpiryStorage.put(admin.getEmail(), LocalDateTime.now().plusMinutes(5));
        
        // In real implementation, send OTP via email/SMS
        logger.info("Generated OTP for {}: {} (This would be sent via email/SMS)", admin.getEmail(), otp);
        
        AdminInfoResponse adminInfo = convertToAdminInfo(admin);
        String otpMessage = String.format("OTP sent to your registered email. Please check your inbox. (Demo OTP: %s)", otp);
        
        return new LoginResponse(adminInfo, otpMessage);
    }

    /**
     * Verify 2FA OTP and complete login
     */
    public LoginResponse verify2FA(TwoFactorRequest request) {
        logger.info("2FA verification attempt for email: {}", request.getEmail());
        
        String storedOtp = otpStorage.get(request.getEmail());
        LocalDateTime expiryTime = otpExpiryStorage.get(request.getEmail());
        
        if (storedOtp == null || expiryTime == null) {
            logger.warn("OTP not found or expired for email: {}", request.getEmail());
            throw new BadCredentialsException("OTP not found or expired. Please login again.");
        }
        
        if (LocalDateTime.now().isAfter(expiryTime)) {
            otpStorage.remove(request.getEmail());
            otpExpiryStorage.remove(request.getEmail());
            logger.warn("OTP expired for email: {}", request.getEmail());
            throw new BadCredentialsException("OTP has expired. Please login again.");
        }
        
        if (!storedOtp.equals(request.getOtp())) {
            logger.warn("Invalid OTP for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid OTP. Please try again.");
        }
        
        // Remove OTP after successful verification
        otpStorage.remove(request.getEmail());
        otpExpiryStorage.remove(request.getEmail());
        
        Admin admin = adminRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Admin not found"));

        logger.info("2FA verification successful for admin: {}", request.getEmail());
        return generateTokenResponse(admin);
    }

    /**
     * Generate token response for successful authentication
     */
    private LoginResponse generateTokenResponse(Admin admin) {
        // Add admin role to token claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", admin.getRole().name());
        claims.put("adminId", admin.getId());
        
        String accessToken = jwtService.generateToken(claims, admin);
        String refreshToken = jwtService.generateRefreshToken(admin);

        AdminInfoResponse adminInfo = convertToAdminInfo(admin);
        
        return new LoginResponse(accessToken, refreshToken, jwtExpiration, adminInfo);
    }

    /**
     * Refresh access token using refresh token
     */
    public TokenResponse refreshToken(String refreshToken) {
        logger.debug("Token refresh attempt");
        
        if (isTokenBlacklisted(refreshToken)) {
            logger.warn("Attempt to use blacklisted refresh token");
            throw new BadCredentialsException("Refresh token is blacklisted");
        }

        try {
            String email = jwtService.extractUsername(refreshToken);
            Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Admin not found"));

            if (!jwtService.isTokenValid(refreshToken, admin)) {
                logger.warn("Invalid refresh token for admin: {}", email);
                throw new BadCredentialsException("Invalid refresh token");
            }

            // Add claims to new token
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", admin.getRole().name());
            claims.put("adminId", admin.getId());
            
            String newAccessToken = jwtService.generateToken(claims, admin);
            
            logger.debug("Token refresh successful for admin: {}", email);
            return new TokenResponse(newAccessToken, jwtExpiration);
            
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new BadCredentialsException("Token refresh failed");
        }
    }

    /**
     * Logout admin by blacklisting token
     */
    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistedTokens.put(token, LocalDateTime.now());
            
            try {
                String email = jwtService.extractUsername(token);
                logger.info("Logout successful for admin: {}", email);
            } catch (Exception e) {
                logger.debug("Could not extract email from token during logout");
            }
        }
        
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    /**
     * Get current authenticated admin information
     */
    public AdminInfoResponse getCurrentAdminInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("No authenticated admin found");
        }

        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("Admin not found"));

        return convertToAdminInfo(admin);
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    /**
     * Determine if 2FA should be required (demo logic)
     */
    private boolean shouldRequire2FA(Admin admin) {
        // For demo purposes, randomly require 2FA (20% chance)
        // In production, this could be based on:
        // - Admin role (e.g., always for SUPER_ADMIN)
        // - IP address changes
        // - Time since last login
        // - Admin preference settings
        return new Random().nextInt(5) == 0; // 20% chance
    }

    /**
     * Generate 6-digit OTP
     */
    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Convert Admin entity to AdminInfoResponse DTO
     */
    private AdminInfoResponse convertToAdminInfo(Admin admin) {
        return new AdminInfoResponse(
            admin.getId(),
            admin.getEmail(),
            admin.getFirstName(),
            admin.getLastName(),
            admin.getRole().name(),
            admin.getIsActive(),
            admin.getLastLogin(),
            admin.getCreatedAt()
        );
    }

    /**
     * Clean up expired OTPs and blacklisted tokens periodically
     */
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        
        // Remove expired OTPs
        otpExpiryStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
        otpStorage.entrySet().removeIf(entry -> !otpExpiryStorage.containsKey(entry.getKey()));
        
        // Remove old blacklisted tokens (older than 24 hours)
        LocalDateTime dayAgo = now.minusHours(24);
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(dayAgo));
        
        logger.debug("Cleaned up expired OTPs and old blacklisted tokens");
    }
}