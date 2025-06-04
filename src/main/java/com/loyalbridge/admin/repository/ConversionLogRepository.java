package com.loyalbridge.admin.repository;

import com.loyalbridge.admin.model.ConversionLog;
import com.loyalbridge.admin.model.ConversionStatus;
import com.loyalbridge.admin.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ConversionLog entity operations
 * 
 * @author LoyalBridge Development Team
 */
@Repository
public interface ConversionLogRepository extends JpaRepository<ConversionLog, Long> {
    
    /**
     * Find conversions with advanced search criteria
     */
    @Query("SELECT c FROM ConversionLog c WHERE " +
           "(:userId IS NULL OR c.userId = :userId) AND " +
           "(:partnerId IS NULL OR c.partnerId = :partnerId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:transactionType IS NULL OR c.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate)")
    Page<ConversionLog> findConversionsWithCriteria(@Param("userId") Long userId,
                                                   @Param("partnerId") Long partnerId,
                                                   @Param("status") ConversionStatus status,
                                                   @Param("transactionType") TransactionType transactionType,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);

    /**
     * Find conversions for export (no pagination)
     */
    @Query("SELECT c FROM ConversionLog c WHERE " +
           "(:userId IS NULL OR c.userId = :userId) AND " +
           "(:partnerId IS NULL OR c.partnerId = :partnerId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:transactionType IS NULL OR c.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate)")
    List<ConversionLog> findConversionsForExport(@Param("userId") Long userId,
                                               @Param("partnerId") Long partnerId,
                                               @Param("status") ConversionStatus status,
                                               @Param("transactionType") TransactionType transactionType,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find conversions by user
     */
    Page<ConversionLog> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find conversions by partner
     */
    Page<ConversionLog> findByPartnerId(Long partnerId, Pageable pageable);
    
    /**
     * Find conversions by status
     */
    List<ConversionLog> findByStatus(ConversionStatus status);
    
    /**
     * Find recent conversions (last 10)
     */
    @Query("SELECT c FROM ConversionLog c ORDER BY c.createdAt DESC")
    Page<ConversionLog> findRecentConversions(Pageable pageable);
    
    /**
     * Count conversions by status
     */
    long countByStatus(ConversionStatus status);
    
    /**
     * Count conversions after date
     */
    @Query("SELECT COUNT(c) FROM ConversionLog c WHERE c.createdAt >= :startDate")
    long countConversionsAfter(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Sum points amount by status
     */
    @Query("SELECT COALESCE(SUM(c.pointsAmount), 0) FROM ConversionLog c WHERE c.status = :status")
    BigDecimal sumPointsAmountByStatus(@Param("status") ConversionStatus status);
    
    /**
     * Sum converted amount by status
     */
    @Query("SELECT COALESCE(SUM(c.convertedAmount), 0) FROM ConversionLog c WHERE c.status = :status")
    BigDecimal sumConvertedAmountByStatus(@Param("status") ConversionStatus status);
    
    /**
     * Get daily conversion statistics
     */
    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count, SUM(c.pointsAmount) as totalPoints " +
           "FROM ConversionLog c WHERE c.createdAt >= :startDate " +
           "GROUP BY DATE(c.createdAt) ORDER BY date DESC")
    List<Object[]> getDailyConversionStats(@Param("startDate") LocalDateTime startDate);
}