package com.loyalbridge.admin.service;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.model.*;
import com.loyalbridge.admin.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for dashboard analytics and overview data
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final UserService userService;
    private final PartnerService partnerService;
    private final ConversionLogRepository conversionLogRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    public DashboardService(UserService userService,
            PartnerService partnerService,
            ConversionLogRepository conversionLogRepository,
            UserRepository userRepository,
            PartnerRepository partnerRepository) {
        this.userService = userService;
        this.partnerService = partnerService;
        this.conversionLogRepository = conversionLogRepository;
        this.userRepository = userRepository;
        this.partnerRepository = partnerRepository;
    }

    /**
     * Get comprehensive dashboard overview
     */
    public DashboardResponse getDashboardOverview() {
        logger.debug("Getting dashboard overview");

        DashboardResponse dashboard = new DashboardResponse();

        // Get user statistics
        dashboard.setUserStats(userService.getUserStats());

        // Get partner statistics
        dashboard.setPartnerStats(partnerService.getPartnerStats());

        // Get conversion statistics
        dashboard.setConversionStats(getConversionStats());

        // Get recent transactions
        dashboard.setRecentTransactions(getRecentTransactions(10));

        // Get conversion trends (last 7 days)
        dashboard.setConversionTrends(getConversionTrends(7));

        // Get system health
        dashboard.setSystemHealth(getSystemHealth());

        return dashboard;
    }

    /**
     * Get conversion statistics
     */
    public ConversionStatsResponse getConversionStats() {
        logger.debug("Getting conversion statistics");

        ConversionStatsResponse stats = new ConversionStatsResponse();

        stats.setTotalConversions(conversionLogRepository.count());
        stats.setCompletedConversions(conversionLogRepository.countByStatus(ConversionStatus.COMPLETED));
        stats.setPendingConversions(conversionLogRepository.countByStatus(ConversionStatus.PENDING));
        stats.setFailedConversions(conversionLogRepository.countByStatus(ConversionStatus.FAILED));

        // Calculate totals for completed conversions only
        BigDecimal totalPoints = conversionLogRepository.sumPointsAmountByStatus(ConversionStatus.COMPLETED);
        BigDecimal totalAmount = conversionLogRepository.sumConvertedAmountByStatus(ConversionStatus.COMPLETED);

        stats.setTotalPointsConverted(totalPoints != null ? totalPoints : BigDecimal.ZERO);
        stats.setTotalAmountConverted(totalAmount != null ? totalAmount : BigDecimal.ZERO);

        // Calculate success rate
        if (stats.getTotalConversions() > 0) {
            BigDecimal successRate = BigDecimal.valueOf(stats.getCompletedConversions())
                    .divide(BigDecimal.valueOf(stats.getTotalConversions()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            stats.setConversionSuccessRate(successRate);
        } else {
            stats.setConversionSuccessRate(BigDecimal.ZERO);
        }

        // Get today's conversions
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        stats.setTodayConversions(conversionLogRepository.countConversionsAfter(todayStart));

        // Get weekly conversions
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        stats.setWeeklyConversions(conversionLogRepository.countConversionsAfter(weekStart));

        return stats;
    }

    /**
     * Get recent transactions
     */
    public List<RecentTransactionResponse> getRecentTransactions(int limit) {
        logger.debug("Getting recent transactions (limit: {})", limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        return conversionLogRepository.findRecentConversions(pageable)
                .getContent()
                .stream()
                .map(this::convertToRecentTransaction)
                .toList();
    }

    /**
     * Get conversion trends for specified number of days
     */
    public List<ConversionTrendResponse> getConversionTrends(int days) {
        logger.debug("Getting conversion trends for {} days", days);

        try {
            LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();
            List<Object[]> dailyStats = conversionLogRepository.getDailyConversionStats(startDate);

            List<ConversionTrendResponse> trends = new ArrayList<>();

            for (Object[] stat : dailyStats) {
                // Handle the native query results properly
                LocalDate date;
                if (stat[0] instanceof java.sql.Date) {
                    date = ((java.sql.Date) stat[0]).toLocalDate();
                } else if (stat[0] instanceof String) {
                    date = LocalDate.parse((String) stat[0]);
                } else {
                    continue; // Skip invalid entries
                }

                Long count = ((Number) stat[1]).longValue();
                BigDecimal totalPoints = stat[2] != null ? new BigDecimal(stat[2].toString()) : BigDecimal.ZERO;
                BigDecimal totalAmount = stat[3] != null ? new BigDecimal(stat[3].toString()) : BigDecimal.ZERO;

                trends.add(new ConversionTrendResponse(date, count, totalPoints, totalAmount));
            }

            return trends;

        } catch (Exception e) {
            logger.error("Error getting conversion trends: {}", e.getMessage(), e);
            // Return empty list for now - this allows the dashboard to still load
            return new ArrayList<>();
        }
    }

    /**
     * Get system health status
     */
    public SystemHealthResponse getSystemHealth() {
        logger.debug("Getting system health status");

        SystemHealthResponse health = new SystemHealthResponse();

        try {
            // Check database connectivity
            long userCount = userRepository.count();
            health.setDatabaseStatus("Connected");
            health.setStatus("Healthy");

            // System metrics
            health.setActiveUsers(userRepository.countByStatus(UserStatus.ACTIVE));
            health.setActiveSessions(1L); // Mock value - would be from session store

            // Memory usage
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double usagePercent = (double) usedMemory / totalMemory * 100;

            health.setMemoryUsage(String.format("%.1f%% (%d MB / %d MB)",
                    usagePercent, usedMemory / 1024 / 1024, totalMemory / 1024 / 1024));

            // Uptime (mock - would be from application start time)
            health.setUptime(LocalDateTime.now().minusHours(2));

        } catch (Exception e) {
            logger.error("Error getting system health: {}", e.getMessage());
            health.setStatus("Degraded");
            health.setDatabaseStatus("Error: " + e.getMessage());
        }

        return health;
    }

    /**
     * Convert ConversionLog to RecentTransactionResponse
     */
    private RecentTransactionResponse convertToRecentTransaction(ConversionLog log) {
        RecentTransactionResponse transaction = new RecentTransactionResponse();
        transaction.setId(log.getId());
        transaction.setType(log.getTransactionType().name());
        transaction.setPointsAmount(log.getPointsAmount());
        transaction.setConvertedAmount(log.getConvertedAmount());
        transaction.setStatus(log.getStatus().name());
        transaction.setTimestamp(log.getCreatedAt());

        // Get user name
        userRepository.findById(log.getUserId())
                .ifPresent(user -> transaction.setUserName(user.getName()));

        // Get partner name
        partnerRepository.findById(log.getPartnerId())
                .ifPresent(partner -> transaction.setPartnerName(partner.getName()));

        return transaction;
    }
}