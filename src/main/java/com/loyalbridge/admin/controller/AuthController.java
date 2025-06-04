package com.loyalbridge.admin.controller;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for admin login, logout, and token management
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Admin authentication and token management endpoints")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Admin login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Authenticate admin with email and password. May require 2FA verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login request received for email: {}", request.getEmail());

            LoginResponse response = authService.login(request);

            if (response.isRequires2FA()) {
                return ResponseEntity.ok(
                        com.loyalbridge.admin.dto.ApiResponse.success(
                                "2FA required. Please verify OTP to complete login.",
                                response));
            } else {
                return ResponseEntity.ok(
                        com.loyalbridge.admin.dto.ApiResponse.success(
                                "Login successful",
                                response));
            }

        } catch (Exception e) {
            logger.error("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                            "Login failed: " + e.getMessage()));
        }
    }

    /**
     * 2FA verification endpoint
     */
    @PostMapping("/verify-2fa")
    @Operation(summary = "Verify 2FA OTP", description = "Verify OTP for two-factor authentication and complete login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @ApiResponse(responseCode = "401", description = "2FA verification failed")
    })
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<LoginResponse>> verify2FA(
            @Valid @RequestBody TwoFactorRequest request) {
        try {
            logger.info("2FA verification request for email: {}", request.getEmail());

            LoginResponse response = authService.verify2FA(request);

            return ResponseEntity.ok(
                    com.loyalbridge.admin.dto.ApiResponse.success(
                            "2FA verification successful",
                            response));

        } catch (Exception e) {
            logger.error("2FA verification failed for email: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                            "2FA verification failed: " + e.getMessage()));
        }
    }

    /**
     * Token refresh endpoint
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Token refresh failed")
    })
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        try {
            logger.debug("Token refresh request received");

            TokenResponse response = authService.refreshToken(request.getRefreshToken());

            return ResponseEntity.ok(
                    com.loyalbridge.admin.dto.ApiResponse.success(
                            "Token refreshed successfully",
                            response));

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                            "Token refresh failed: " + e.getMessage()));
        }
    }

    /**
     * Admin logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "Admin logout", description = "Logout current admin session and blacklist token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Logout failed")
    })
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<String>> logout(
            @Parameter(description = "Authorization header with Bearer token") @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            authService.logout(token);

            return ResponseEntity.ok(
                    com.loyalbridge.admin.dto.ApiResponse.success(
                            "Logout successful",
                            null));

        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                            "Logout failed: " + e.getMessage()));
        }
    }

    /**
     * Get current admin info endpoint
     */
    @GetMapping("/me")
    @Operation(summary = "Get current admin info", description = "Get current authenticated admin information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin info retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<AdminInfoResponse>> getCurrentAdmin() {
        try {
            AdminInfoResponse response = authService.getCurrentAdminInfo();

            return ResponseEntity.ok(
                    com.loyalbridge.admin.dto.ApiResponse.success(
                            "Admin info retrieved successfully",
                            response));

        } catch (Exception e) {
            logger.error("Failed to get current admin info: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(com.loyalbridge.admin.dto.ApiResponse.error(
                            "Failed to get admin info: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication health check", description = "Check if authentication service is working")
    public ResponseEntity<com.loyalbridge.admin.dto.ApiResponse<String>> health() {
        return ResponseEntity.ok(
                com.loyalbridge.admin.dto.ApiResponse.success(
                        "Authentication service is healthy",
                        "OK"));
    }
}