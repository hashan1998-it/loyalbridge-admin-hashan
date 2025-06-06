package com.loyalbridge.admin.service;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.model.*;
import com.loyalbridge.admin.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for user management operations
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    private final PartnerRepository partnerRepository;

    public UserService(UserRepository userRepository,
                      PointsHistoryRepository pointsHistoryRepository,
                      PartnerRepository partnerRepository) {
        this.userRepository = userRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
        this.partnerRepository = partnerRepository;
    }

    /**
     * Get all users with search criteria and pagination
     */
    public Page<UserResponse> getAllUsers(UserSearchCriteria criteria, Pageable pageable) {
        logger.debug("Getting users with criteria: {}", criteria);
        
        UserStatus status = null;
        if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
            try {
                status = UserStatus.valueOf(criteria.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided: {}", criteria.getStatus());
            }
        }
        
        return userRepository.findUsersWithCriteria(
            criteria.getName(),
            criteria.getPhone(),
            status,
            criteria.getIsHighRisk(),
            criteria.getIsVerified(),
            pageable
        ).map(this::convertToUserResponse);
    }

    /**
     * Get user by ID with detailed information
     */
    public UserDetailResponse getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        return convertToUserDetailResponse(user);
    }

    /**
     * Get user points history
     */
    public Page<PointsHistoryResponse> getUserPointsHistory(Long userId, Pageable pageable) {
        logger.debug("Getting points history for user: {}", userId);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        return pointsHistoryRepository.findByUserId(userId, pageable)
            .map(this::convertToPointsHistoryResponse);
    }

    /**
     * Update user status
     */
    public UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        logger.info("Updating status for user {} to {}", id, request.getStatus());
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        UserStatus oldStatus = user.getStatus();
        UserStatus newStatus = UserStatus.valueOf(request.getStatus().toUpperCase());
        
        user.setStatus(newStatus);
        user = userRepository.save(user);
        
        // Log the status change
        logger.info("User {} status changed from {} to {} by admin. Reason: {}", 
                   id, oldStatus, newStatus, request.getReason());
        
        // Create audit log entry if needed
        createStatusChangeAuditLog(user, oldStatus, newStatus, request.getReason());
        
        return convertToUserResponse(user);
    }

    /**
     * Update user risk flag
     */
    public UserResponse updateUserRiskFlag(Long id, UpdateRiskFlagRequest request) {
        logger.info("Updating risk flag for user {} to {}", id, request.getIsHighRisk());
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        Boolean oldRiskFlag = user.getIsHighRisk();
        user.setIsHighRisk(request.getIsHighRisk());
        user = userRepository.save(user);
        
        logger.info("User {} risk flag changed from {} to {} by admin. Reason: {}", 
                   id, oldRiskFlag, request.getIsHighRisk(), request.getReason());
        
        return convertToUserResponse(user);
    }

    /**
     * Update user verification status
     */
    public UserResponse updateUserVerification(Long id, UpdateVerificationRequest request) {
        logger.info("Updating verification for user {} to {}", id, request.getIsVerified());
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        Boolean oldVerification = user.getIsVerified();
        user.setIsVerified(request.getIsVerified());
        user = userRepository.save(user);
        
        logger.info("User {} verification changed from {} to {} by admin. Notes: {}", 
                   id, oldVerification, request.getIsVerified(), request.getNotes());
        
        return convertToUserResponse(user);
    }

    /**
     * Export users to CSV
     */
    public byte[] exportUsersToCSV(UserSearchCriteria criteria) {
        logger.info("Exporting users to CSV with criteria: {}", criteria);
        
        UserStatus status = null;
        if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
            try {
                status = UserStatus.valueOf(criteria.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided for export: {}", criteria.getStatus());
            }
        }
        
        List<User> users = userRepository.findUsersForExport(
            criteria.getName(),
            criteria.getPhone(),
            status,
            criteria.getIsHighRisk(),
            criteria.getIsVerified()
        );
        
        return generateCSV(users);
    }

    /**
     * Get user statistics
     */
    public UserStatsResponse getUserStats() {
        logger.debug("Getting user statistics");
        
        UserStatsResponse stats = new UserStatsResponse();
        
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByStatus(UserStatus.ACTIVE));
        stats.setFrozenUsers(userRepository.countByStatus(UserStatus.FROZEN));
        stats.setSuspendedUsers(userRepository.countByStatus(UserStatus.SUSPENDED));
        stats.setInactiveUsers(userRepository.countByStatus(UserStatus.INACTIVE));
        stats.setVerifiedUsers(userRepository.countByIsVerified(true));
        stats.setHighRiskUsers(userRepository.countByIsHighRisk(true));
        
        stats.setTotalPointsInSystem(userRepository.sumTotalPoints() != null ? 
            userRepository.sumTotalPoints() : BigDecimal.ZERO);
        stats.setTotalLifetimeEarnings(userRepository.sumLifetimeEarnings() != null ? 
            userRepository.sumLifetimeEarnings() : BigDecimal.ZERO);
        stats.setTotalLifetimeRedemptions(userRepository.sumLifetimeRedemptions() != null ? 
            userRepository.sumLifetimeRedemptions() : BigDecimal.ZERO);
        
        return stats;
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setStatus(user.getStatus().name());
        response.setTotalPoints(user.getTotalPoints());
        response.setLifetimeEarnings(user.getLifetimeEarnings());
        response.setLifetimeRedemptions(user.getLifetimeRedemptions());
        response.setIsHighRisk(user.getIsHighRisk());
        response.setIsVerified(user.getIsVerified());
        response.setLastActivity(user.getLastActivity());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    /**
     * Convert User entity to UserDetailResponse DTO
     */
    private UserDetailResponse convertToUserDetailResponse(User user) {
        UserDetailResponse response = new UserDetailResponse();
        
        // Copy basic fields
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setStatus(user.getStatus().name());
        response.setTotalPoints(user.getTotalPoints());
        response.setLifetimeEarnings(user.getLifetimeEarnings());
        response.setLifetimeRedemptions(user.getLifetimeRedemptions());
        response.setIsHighRisk(user.getIsHighRisk());
        response.setIsVerified(user.getIsVerified());
        response.setLastActivity(user.getLastActivity());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        // Add detailed fields
        response.setTotalTransactions(getTotalTransactionsForUser(user.getId()));
        response.setRiskReason(user.getIsHighRisk() ? "Flagged by admin" : null);
        response.setVerificationNotes(user.getIsVerified() ? "Verified by admin" : "Pending verification");
        
        return response;
    }

    /**
     * Convert PointsHistory entity to PointsHistoryResponse DTO
     */
    private PointsHistoryResponse convertToPointsHistoryResponse(PointsHistory history) {
        PointsHistoryResponse response = new PointsHistoryResponse();
        response.setId(history.getId());
        response.setType(history.getTransactionType().name());
        response.setAmount(history.getAmount());
        response.setBalanceAfter(history.getBalanceAfter());
        response.setDescription(history.getDescription());
        response.setReferenceId(history.getReferenceId());
        response.setCreatedAt(history.getCreatedAt());
        
        // Get partner name if available
        if (history.getPartnerId() != null) {
            partnerRepository.findById(history.getPartnerId())
                .ifPresent(partner -> response.setPartnerName(partner.getName()));
        }
        
        return response;
    }

    /**
     * Get total transactions count for user
     */
    private Integer getTotalTransactionsForUser(Long userId) {
        // This would typically be a count query on transaction tables
        // For now, we'll use points history as a proxy
        Pageable pageable = PageRequest.of(0, 1);
        return (int) pointsHistoryRepository.findByUserId(userId, pageable).getTotalElements();
    }

    /**
     * Create status change audit log
     */
    private void createStatusChangeAuditLog(User user, UserStatus oldStatus, UserStatus newStatus, String reason) {
        // In a real implementation, this would create an audit log entry
        // For now, we'll just log it
        logger.info("AUDIT: User {} status changed from {} to {} - Reason: {}", 
                   user.getId(), oldStatus, newStatus, reason);
    }

    /**
     * Generate CSV from user list
     */
    private byte[] generateCSV(List<User> users) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Name,Email,Phone,Status,Total Points,Lifetime Earnings,Lifetime Redemptions,High Risk,Verified,Last Activity,Created At");
            
            // CSV Data
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (User user : users) {
                writer.printf("%d,%s,%s,%s,%s,%.2f,%.2f,%.2f,%s,%s,%s,%s%n",
                    user.getId(),
                    escapeCSV(user.getName()),
                    escapeCSV(user.getEmail()),
                    escapeCSV(user.getPhoneNumber()),
                    user.getStatus().name(),
                    user.getTotalPoints(),
                    user.getLifetimeEarnings(),
                    user.getLifetimeRedemptions(),
                    user.getIsHighRisk(),
                    user.getIsVerified(),
                    user.getLastActivity() != null ? user.getLastActivity().format(formatter) : "",
                    user.getCreatedAt().format(formatter)
                );
            }
            
            writer.flush();
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating CSV: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV export", e);
        }
    }

    /**
     * Escape CSV special characters
     */
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}