package com.loyalbridge.admin.controller;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user management operations
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing loyalty program users")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users with pagination and filtering
     */
    @GetMapping
    @Operation(summary = "Get all users", 
               description = "Retrieve paginated list of users with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<Page<UserResponse>>> getAllUsers(
            @Parameter(description = "Search by name") @RequestParam(required = false) String name,
            @Parameter(description = "Search by phone number") @RequestParam(required = false) String phone,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by high risk flag") @RequestParam(required = false) Boolean isHighRisk,
            @Parameter(description = "Filter by verification status") @RequestParam(required = false) Boolean isVerified,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            logger.debug("Getting users - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
            
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            UserSearchCriteria criteria = new UserSearchCriteria();
            criteria.setName(name);
            criteria.setPhone(phone);
            criteria.setStatus(status);
            criteria.setIsHighRisk(isHighRisk);
            criteria.setIsVerified(isVerified);
            
            Page<UserResponse> users = userService.getAllUsers(criteria, pageable);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Users retrieved successfully", 
                    users
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve users: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", 
               description = "Retrieve detailed user information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<UserDetailResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            logger.debug("Getting user by ID: {}", id);
            
            UserDetailResponse user = userService.getUserById(id);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "User retrieved successfully", 
                    user
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve user: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get user points history
     */
    @GetMapping("/{id}/points-history")
    @Operation(summary = "Get user points history", 
               description = "Retrieve user's point transaction history with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Points history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<Page<PointsHistoryResponse>>> getUserPointsHistory(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            logger.debug("Getting points history for user: {}", id);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<PointsHistoryResponse> history = userService.getUserPointsHistory(id, pageable);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Points history retrieved successfully", 
                    history
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve points history for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve points history: " + e.getMessage()
                    ));
        }
    }

    /**
     * Update user status
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update user status", 
               description = "Update user account status (ACTIVE, FROZEN, SUSPENDED, INACTIVE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<UserResponse>> updateUserStatus(
            @Parameter(description = "User ID") @PathVariable Long id, 
            @Valid @RequestBody UpdateUserStatusRequest request) {
        try {
            logger.info("Updating status for user {} to {}", id, request.getStatus());
            
            UserResponse user = userService.updateUserStatus(id, request);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "User status updated successfully", 
                    user
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to update status for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to update user status: " + e.getMessage()
                    ));
        }
    }

    /**
     * Update user risk flag
     */
    @PutMapping("/{id}/risk-flag")
    @Operation(summary = "Update user risk flag", 
               description = "Flag or unflag user as high risk")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User risk flag updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<UserResponse>> updateUserRiskFlag(
            @Parameter(description = "User ID") @PathVariable Long id, 
            @Valid @RequestBody UpdateRiskFlagRequest request) {
        try {
            logger.info("Updating risk flag for user {} to {}", id, request.getIsHighRisk());
            
            UserResponse user = userService.updateUserRiskFlag(id, request);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "User risk flag updated successfully", 
                    user
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to update risk flag for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to update user risk flag: " + e.getMessage()
                    ));
        }
    }

    /**
     * Update user verification status
     */
    @PutMapping("/{id}/verification")
    @Operation(summary = "Update user verification", 
               description = "Verify or unverify user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User verification updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<UserResponse>> updateUserVerification(
            @Parameter(description = "User ID") @PathVariable Long id, 
            @Valid @RequestBody UpdateVerificationRequest request) {
        try {
            logger.info("Updating verification for user {} to {}", id, request.getIsVerified());
            
            UserResponse user = userService.updateUserVerification(id, request);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "User verification updated successfully", 
                    user
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to update verification for user {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to update user verification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Export users to CSV
     */
    @GetMapping("/export")
    @Operation(summary = "Export users to CSV", 
               description = "Export filtered users to CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV export generated successfully"),
        @ApiResponse(responseCode = "400", description = "Export failed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'FINANCE_TEAM')")
    public ResponseEntity<byte[]> exportUsers(
            @Parameter(description = "Search by name") @RequestParam(required = false) String name,
            @Parameter(description = "Search by phone") @RequestParam(required = false) String phone,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by high risk flag") @RequestParam(required = false) Boolean isHighRisk,
            @Parameter(description = "Filter by verification status") @RequestParam(required = false) Boolean isVerified) {
        try {
            logger.info("Exporting users to CSV with filters");
            
            UserSearchCriteria criteria = new UserSearchCriteria();
            criteria.setName(name);
            criteria.setPhone(phone);
            criteria.setStatus(status);
            criteria.setIsHighRisk(isHighRisk);
            criteria.setIsVerified(isVerified);
            
            byte[] csvData = userService.exportUsersToCSV(criteria);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                "loyalbridge_users_export_" + System.currentTimeMillis() + ".csv");
            
            return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Failed to export users: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", 
               description = "Retrieve comprehensive user statistics for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<UserStatsResponse>> getUserStats() {
        try {
            logger.debug("Getting user statistics");
            
            UserStatsResponse stats = userService.getUserStats();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "User statistics retrieved successfully", 
                    stats
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve user statistics: " + e.getMessage()
                    ));
        }
    }

    /**
     * Search users (alternative endpoint for advanced search)
     */
    @PostMapping("/search")
    @Operation(summary = "Advanced user search", 
               description = "Search users with complex criteria via POST body")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search criteria"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUPPORT_STAFF', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<Page<UserResponse>>> searchUsers(
            @Valid @RequestBody UserSearchCriteria criteria,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            logger.debug("Advanced user search with criteria: {}", criteria);
            
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UserResponse> users = userService.getAllUsers(criteria, pageable);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Users found successfully", 
                    users
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to search users: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to search users: " + e.getMessage()
                    ));
        }
    }
}