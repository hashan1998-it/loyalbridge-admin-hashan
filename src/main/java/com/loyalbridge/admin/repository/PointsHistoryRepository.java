package com.loyalbridge.admin.repository;

import com.loyalbridge.admin.model.PointsHistory;
import com.loyalbridge.admin.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repository interface for PointsHistory entity operations
 * 
 * @author LoyalBridge Development Team
 */
@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {
    
    /**
     * Find points history by user
     */
    Page<PointsHistory> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find points history by user and transaction type
     */
    Page<PointsHistory> findByUserIdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);
    
    /**
     * Find points history by partner
     */
    Page<PointsHistory> findByPartnerId(Long partnerId, Pageable pageable);
    
    /**
     * Find points history within date range
     */
    @Query("SELECT p FROM PointsHistory p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<PointsHistory> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);
    
    /**
     * Sum points by user and transaction type
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PointsHistory p " +
           "WHERE p.userId = :userId AND p.transactionType = :transactionType")
    BigDecimal sumPointsByUserAndType(@Param("userId") Long userId, 
                                    @Param("transactionType") TransactionType transactionType);
    
    /**
     * Get user's latest balance
     */
    @Query("SELECT p.balanceAfter FROM PointsHistory p WHERE p.userId = :userId " +
           "ORDER BY p.createdAt DESC")
    Page<BigDecimal> getLatestBalance(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find recent transactions for user
     */
    @Query("SELECT p FROM PointsHistory p WHERE p.userId = :userId " +
           "ORDER BY p.createdAt DESC")
    Page<PointsHistory> findRecentTransactionsByUser(@Param("userId") Long userId, Pageable pageable);
}