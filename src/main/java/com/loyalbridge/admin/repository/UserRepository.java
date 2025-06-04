package com.loyalbridge.admin.repository;

import com.loyalbridge.admin.model.User;
import com.loyalbridge.admin.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 * 
 * @author LoyalBridge Development Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users with advanced search criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:phone IS NULL OR u.phoneNumber LIKE CONCAT('%', :phone, '%')) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:isHighRisk IS NULL OR u.isHighRisk = :isHighRisk) AND " +
           "(:isVerified IS NULL OR u.isVerified = :isVerified)")
    Page<User> findUsersWithCriteria(@Param("name") String name,
                                   @Param("phone") String phone,
                                   @Param("status") UserStatus status,
                                   @Param("isHighRisk") Boolean isHighRisk,
                                   @Param("isVerified") Boolean isVerified,
                                   Pageable pageable);

    /**
     * Find users for export (no pagination)
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:phone IS NULL OR u.phoneNumber LIKE CONCAT('%', :phone, '%')) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:isHighRisk IS NULL OR u.isHighRisk = :isHighRisk) AND " +
           "(:isVerified IS NULL OR u.isVerified = :isVerified)")
    List<User> findUsersForExport(@Param("name") String name,
                                @Param("phone") String phone,
                                @Param("status") UserStatus status,
                                @Param("isHighRisk") Boolean isHighRisk,
                                @Param("isVerified") Boolean isVerified);

    /**
     * Count users by status
     */
    long countByStatus(UserStatus status);
    
    /**
     * Count verified users
     */
    long countByIsVerified(boolean isVerified);
    
    /**
     * Count high-risk users
     */
    long countByIsHighRisk(boolean isHighRisk);
    
    /**
     * Sum total points in system
     */
    @Query("SELECT COALESCE(SUM(u.totalPoints), 0) FROM User u")
    BigDecimal sumTotalPoints();
    
    /**
     * Sum lifetime earnings
     */
    @Query("SELECT COALESCE(SUM(u.lifetimeEarnings), 0) FROM User u")
    BigDecimal sumLifetimeEarnings();
    
    /**
     * Sum lifetime redemptions
     */
    @Query("SELECT COALESCE(SUM(u.lifetimeRedemptions), 0) FROM User u")
    BigDecimal sumLifetimeRedemptions();
    
    /**
     * Find users with points greater than amount
     */
    List<User> findByTotalPointsGreaterThan(BigDecimal amount);
    
    /**
     * Find users active since date
     */
    @Query("SELECT u FROM User u WHERE u.lastActivity >= :since")
    List<User> findUsersActiveSince(@Param("since") LocalDateTime since);
    
    /**
     * Find top users by points
     */
    @Query("SELECT u FROM User u ORDER BY u.totalPoints DESC")
    Page<User> findTopUsersByPoints(Pageable pageable);
}