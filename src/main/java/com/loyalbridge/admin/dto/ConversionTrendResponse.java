package com.loyalbridge.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for conversion trend data
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
public class ConversionTrendResponse {
    
    private LocalDate date;
    private Long count;
    private BigDecimal totalPoints;
    private BigDecimal totalAmount;

    // Constructors
    public ConversionTrendResponse() {}

    public ConversionTrendResponse(LocalDate date, Long count, BigDecimal totalPoints, BigDecimal totalAmount) {
        this.date = date;
        this.count = count;
        this.totalPoints = totalPoints;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }

    public BigDecimal getTotalPoints() { return totalPoints; }
    public void setTotalPoints(BigDecimal totalPoints) { this.totalPoints = totalPoints; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}