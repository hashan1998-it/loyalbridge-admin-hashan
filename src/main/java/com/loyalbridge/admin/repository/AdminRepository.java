package com.loyalbridge.admin.repository;

import com.loyalbridge.admin.model.Admin;
import com.loyalbridge.admin.model.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Admin entity operations
 * 
 * @author LoyalBridge Development Team
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * Find admin by email address
     */
    Optional<Admin> findByEmail(String email);
    
    /**
     * Check if admin exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active admins
     */
    List<Admin> findByIsActiveTrue();
    
    /**
     * Find admins by role
     */
    List<Admin> findByRole(AdminRole role);
    
    /**
     * Find admins by role and active status
     */
    List<Admin> findByRoleAndIsActive(AdminRole role, Boolean isActive);
    
    /**
     * Count active admins
     */
    @Query("SELECT COUNT(a) FROM Admin a WHERE a.isActive = true")
    long countActiveAdmins();
    
    /**
     * Count admins by role
     */
    long countByRole(AdminRole role);
    
    /**
     * Find admins who logged in after specific date
     */
    @Query("SELECT a FROM Admin a WHERE a.lastLogin >= :since")
    List<Admin> findAdminsLoggedInSince(@Param("since") LocalDateTime since);
    
    /**
     * Find admins by first name or last name containing
     */
    @Query("SELECT a FROM Admin a WHERE " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Admin> findByNameContaining(@Param("name") String name);
}