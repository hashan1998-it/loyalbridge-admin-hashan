package com.loyalbridge.admin.controller;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for dashboard and analytics operations
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard & Analytics", description = "Endpoints for dashboard overview and analytics")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get comprehensive dashboard overview
     */
    @GetMapping("/overview")
    @Operation(summary = "Get dashboard overview", 
               description = "Retrieve comprehensive dashboard data including users, partners, conversions, and trends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard overview retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FINANCE_TEAM', 'SUPPORT_STAFF', 'PARTNER_ADMIN')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<DashboardResponse>> getDashboardOverview() {
        try {
            logger.debug("Getting dashboard overview");
            
            DashboardResponse dashboard = dashboardService.getDashboardOverview();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Dashboard overview retrieved successfully", 
                    dashboard
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve dashboard overview: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve dashboard overview: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get conversion statistics
     */
    @GetMapping("/conversions/stats")
    @Operation(summary = "Get conversion statistics", 
               description = "Retrieve detailed conversion statistics and metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversion statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FINANCE_TEAM', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<ConversionStatsResponse>> getConversionStats() {
        try {
            logger.debug("Getting conversion statistics");
            
            ConversionStatsResponse stats = dashboardService.getConversionStats();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Conversion statistics retrieved successfully", 
                    stats
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve conversion statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve conversion statistics: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get recent transactions
     */
    @GetMapping("/transactions/recent")
    @Operation(summary = "Get recent transactions", 
               description = "Retrieve list of recent conversion transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent transactions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FINANCE_TEAM', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<List<RecentTransactionResponse>>> getRecentTransactions(
            @Parameter(description = "Number of transactions to retrieve") 
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.debug("Getting recent transactions (limit: {})", limit);
            
            List<RecentTransactionResponse> transactions = dashboardService.getRecentTransactions(limit);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Recent transactions retrieved successfully", 
                    transactions
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve recent transactions: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve recent transactions: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get conversion trends
     */
    @GetMapping("/conversions/trends")
    @Operation(summary = "Get conversion trends", 
               description = "Retrieve conversion trends over specified number of days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversion trends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FINANCE_TEAM', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<List<ConversionTrendResponse>>> getConversionTrends(
            @Parameter(description = "Number of days to retrieve trends for") 
            @RequestParam(defaultValue = "7") int days) {
        try {
            logger.debug("Getting conversion trends for {} days", days);
            
            List<ConversionTrendResponse> trends = dashboardService.getConversionTrends(days);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Conversion trends retrieved successfully", 
                    trends
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve conversion trends: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve conversion trends: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get system health
     */
    @GetMapping("/system/health")
    @Operation(summary = "Get system health", 
               description = "Retrieve system health status and metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System health retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<SystemHealthResponse>> getSystemHealth() {
        try {
            logger.debug("Getting system health");
            
            SystemHealthResponse health = dashboardService.getSystemHealth();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "System health retrieved successfully", 
                    health
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve system health: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve system health: " + e.getMessage()
                    ));
        }
    }
}