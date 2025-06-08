package com.loyalbridge.admin.controller;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.service.PartnerService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for partner management operations
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/partners")
@Tag(name = "Partner Management", description = "Endpoints for managing loyalty program partners")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class PartnerController {

    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    /**
     * Get all partners with pagination and filtering
     */
    @GetMapping
    @Operation(summary = "Get all partners", 
               description = "Retrieve paginated list of partners with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partners retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<Page<PartnerResponse>>> getAllPartners(
            @Parameter(description = "Search by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by authentication method") @RequestParam(required = false) String authMethod,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            logger.debug("Getting partners - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
            
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            PartnerSearchCriteria criteria = new PartnerSearchCriteria();
            criteria.setName(name);
            criteria.setAuthMethod(authMethod);
            criteria.setIsActive(isActive);
            
            Page<PartnerResponse> partners = partnerService.getAllPartners(criteria, pageable);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partners retrieved successfully", 
                    partners
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve partners: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve partners: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get partner by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get partner by ID", 
               description = "Retrieve detailed partner information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<PartnerResponse>> getPartnerById(
            @Parameter(description = "Partner ID") @PathVariable Long id) {
        try {
            logger.debug("Getting partner by ID: {}", id);
            
            PartnerResponse partner = partnerService.getPartnerById(id);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partner retrieved successfully", 
                    partner
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve partner {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve partner: " + e.getMessage()
                    ));
        }
    }

    /**
     * Create new partner
     */
    @PostMapping
    @Operation(summary = "Create new partner", 
               description = "Create a new loyalty program partner")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partner created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or partner name exists"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<PartnerResponse>> createPartner(
            @Valid @RequestBody CreatePartnerRequest request) {
        try {
            logger.info("Creating new partner: {}", request.getName());
            
            PartnerResponse partner = partnerService.createPartner(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(com.loyalbridge.admin.dto.ApiResponse.success(
                        "Partner created successfully", 
                        partner
                    ));
            
        } catch (Exception e) {
            logger.error("Failed to create partner: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to create partner: " + e.getMessage()
                    ));
        }
    }

    /**
     * Update partner
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update partner", 
               description = "Update partner information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<PartnerResponse>> updatePartner(
            @Parameter(description = "Partner ID") @PathVariable Long id,
            @Valid @RequestBody UpdatePartnerRequest request) {
        try {
            logger.info("Updating partner: {}", id);
            
            PartnerResponse partner = partnerService.updatePartner(id, request);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partner updated successfully", 
                    partner
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to update partner {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to update partner: " + e.getMessage()
                    ));
        }
    }

    /**
     * Toggle partner status
     */
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle partner status", 
               description = "Enable or disable partner integration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<PartnerResponse>> togglePartnerStatus(
            @Parameter(description = "Partner ID") @PathVariable Long id) {
        try {
            logger.info("Toggling status for partner: {}", id);
            
            PartnerResponse partner = partnerService.togglePartnerStatus(id);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partner status updated successfully", 
                    partner
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to toggle status for partner {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to update partner status: " + e.getMessage()
                    ));
        }
    }

    /**
     * Delete partner
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete partner", 
               description = "Delete partner (only if no conversion logs exist)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete partner with existing conversions"),
        @ApiResponse(responseCode = "404", description = "Partner not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<String>> deletePartner(
            @Parameter(description = "Partner ID") @PathVariable Long id) {
        try {
            logger.info("Deleting partner: {}", id);
            
            partnerService.deletePartner(id);
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partner deleted successfully", 
                    null
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to delete partner {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to delete partner: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get active partners
     */
    @GetMapping("/active")
    @Operation(summary = "Get active partners", 
               description = "Get list of active partners for selection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active partners retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN', 'FINANCE_TEAM', 'SUPPORT_STAFF')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<List<PartnerResponse>>> getActivePartners() {
        try {
            logger.debug("Getting active partners");
            
            List<PartnerResponse> partners = partnerService.getActivePartners();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Active partners retrieved successfully", 
                    partners
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve active partners: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve active partners: " + e.getMessage()
                    ));
        }
    }

    /**
     * Get partner statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get partner statistics", 
               description = "Retrieve comprehensive partner statistics for dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PARTNER_ADMIN', 'FINANCE_TEAM')")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<PartnerStatsResponse>> getPartnerStats() {
        try {
            logger.debug("Getting partner statistics");
            
            PartnerStatsResponse stats = partnerService.getPartnerStats();
            
            return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                    "Partner statistics retrieved successfully", 
                    stats
                )
            );
            
        } catch (Exception e) {
            logger.error("Failed to retrieve partner statistics: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                        "Failed to retrieve partner statistics: " + e.getMessage()
                    ));
        }
    }
}