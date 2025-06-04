package com.loyalbridge.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User entity representing loyalty program participants
 * 
 * Supports user lifecycle management, points tracking, and risk assessment
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_status", columnList = "status"),
    @Index(name = "idx_user_phone", columnList = "phone_number"),
    @Index(name = "idx_user_created_at", columnList = "created_at")
})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "total_points", precision = 12, scale = 2)
    private BigDecimal totalPoints = BigDecimal.ZERO;

    @Column(name = "lifetime_earnings", precision = 12, scale = 2)
    private BigDecimal lifetimeEarnings = BigDecimal.ZERO;

    @Column(name = "lifetime_redemptions", precision = 12, scale = 2)
    private BigDecimal lifetimeRedemptions = BigDecimal.ZERO;

    @Column(name = "is_high_risk")
    private Boolean isHighRisk = false;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {}

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // JPA Lifecycle Callbacks
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.lastActivity == null) {
            this.lastActivity = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business Methods
    public void addPoints(BigDecimal amount) {
        this.totalPoints = this.totalPoints.add(amount);
        this.lifetimeEarnings = this.lifetimeEarnings.add(amount);
        this.lastActivity = LocalDateTime.now();
    }

    public void deductPoints(BigDecimal amount) {
        this.totalPoints = this.totalPoints.subtract(amount);
        this.lifetimeRedemptions = this.lifetimeRedemptions.add(amount);
        this.lastActivity = LocalDateTime.now();
    }

    public boolean hasEnoughPoints(BigDecimal amount) {
        return this.totalPoints.compareTo(amount) >= 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public BigDecimal getTotalPoints() { return totalPoints; }
    public void setTotalPoints(BigDecimal totalPoints) { this.totalPoints = totalPoints; }

    public BigDecimal getLifetimeEarnings() { return lifetimeEarnings; }
    public void setLifetimeEarnings(BigDecimal lifetimeEarnings) { this.lifetimeEarnings = lifetimeEarnings; }

    public BigDecimal getLifetimeRedemptions() { return lifetimeRedemptions; }
    public void setLifetimeRedemptions(BigDecimal lifetimeRedemptions) { this.lifetimeRedemptions = lifetimeRedemptions; }

    public Boolean getIsHighRisk() { return isHighRisk; }
    public void setIsHighRisk(Boolean isHighRisk) { this.isHighRisk = isHighRisk; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", totalPoints=" + totalPoints +
                ", isHighRisk=" + isHighRisk +
                ", isVerified=" + isVerified +
                '}';
    }
}