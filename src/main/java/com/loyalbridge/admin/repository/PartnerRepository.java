package com.loyalbridge.admin.repository;

import com.loyalbridge.admin.model.AuthMethod;
import com.loyalbridge.admin.model.Partner;
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
 * Repository interface for Partner entity operations
 * 
 * @author LoyalBridge Development Team
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {
    
    /**
     * Find partner by name
     */
    Optional<Partner> findByName(String name);
    
    /**
     * Check if partner exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find partners with search criteria
     */
    @Query("SELECT p FROM Partner p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<Partner> findPartnersWithCriteria(@Param("name") String name,
                                         @Param("isActive") Boolean isActive,
                                         Pageable pageable);

    /**
     * Find all active partners
     */
    List<Partner> findByIsActiveTrue();
    
    /**
     * Find partners by authentication method
     */
    List<Partner> findByAuthMethod(AuthMethod authMethod);
    
    /**
     * Count active partners
     */
    long countByIsActive(boolean isActive);
    
    /**
     * Count partners by auth method
     */
    long countByAuthMethod(AuthMethod authMethod);
    
    /**
     * Find partners with conversion rate between values
     */
    List<Partner> findByConversionRateBetween(BigDecimal min, BigDecimal max);
    
    /**
     * Find partners with recent transactions
     */
    @Query("SELECT p FROM Partner p WHERE p.lastTransactionAt >= :since")
    List<Partner> findPartnersWithRecentTransactions(@Param("since") LocalDateTime since);
    
    /**
     * Find top partners by transaction volume
     */
    @Query("SELECT p FROM Partner p ORDER BY p.totalTransactions DESC")
    Page<Partner> findTopPartnersByTransactions(Pageable pageable);
    
    /**
     * Find top partners by amount processed
     */
    @Query("SELECT p FROM Partner p ORDER BY p.totalAmountProcessed DESC")
    Page<Partner> findTopPartnersByAmount(Pageable pageable);
    
    /**
     * Sum total amount processed by all partners
     */
    @Query("SELECT COALESCE(SUM(p.totalAmountProcessed), 0) FROM Partner p WHERE p.isActive = true")
    BigDecimal sumTotalAmountProcessed();
}