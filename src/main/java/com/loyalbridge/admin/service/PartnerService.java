package com.loyalbridge.admin.service;

import com.loyalbridge.admin.dto.*;
import com.loyalbridge.admin.model.*;
import com.loyalbridge.admin.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for partner management operations
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class PartnerService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    private final PartnerRepository partnerRepository;
    private final ConversionLogRepository conversionLogRepository;

    public PartnerService(PartnerRepository partnerRepository,
                         ConversionLogRepository conversionLogRepository) {
        this.partnerRepository = partnerRepository;
        this.conversionLogRepository = conversionLogRepository;
    }

    /**
     * Get all partners with search criteria and pagination
     */
    public Page<PartnerResponse> getAllPartners(PartnerSearchCriteria criteria, Pageable pageable) {
        logger.debug("Getting partners with criteria: {}", criteria);
        
        return partnerRepository.findPartnersWithCriteria(
            criteria.getName(),
            criteria.getIsActive(),
            pageable
        ).map(this::convertToPartnerResponse);
    }

    /**
     * Get partner by ID
     */
    public PartnerResponse getPartnerById(Long id) {
        logger.debug("Getting partner by ID: {}", id);
        
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        
        return convertToPartnerResponse(partner);
    }

    /**
     * Create new partner
     */
    public PartnerResponse createPartner(CreatePartnerRequest request) {
        logger.info("Creating new partner: {}", request.getName());
        
        // Check if partner name already exists
        if (partnerRepository.existsByName(request.getName())) {
            throw new RuntimeException("Partner with name '" + request.getName() + "' already exists");
        }
        
        Partner partner = new Partner();
        partner.setName(request.getName());
        partner.setDescription(request.getDescription());
        partner.setApiUrl(request.getApiUrl());
        partner.setAuthMethod(AuthMethod.valueOf(request.getAuthMethod().toUpperCase()));
        partner.setConversionRate(request.getConversionRate());
        partner.setWebhookUrl(request.getWebhookUrl());
        partner.setConnectionTimeout(request.getConnectionTimeout());
        partner.setReadTimeout(request.getReadTimeout());
        partner.setIsActive(true);
        
        // Generate API key
        partner.setApiKey(generateApiKey());
        
        partner = partnerRepository.save(partner);
        
        logger.info("Partner created successfully: {} (ID: {})", partner.getName(), partner.getId());
        return convertToPartnerResponse(partner);
    }

    /**
     * Update partner
     */
    public PartnerResponse updatePartner(Long id, UpdatePartnerRequest request) {
        logger.info("Updating partner: {}", id);
        
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        
        // Update only provided fields
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            // Check if new name conflicts with existing partner
            if (!partner.getName().equals(request.getName()) && 
                partnerRepository.existsByName(request.getName())) {
                throw new RuntimeException("Partner with name '" + request.getName() + "' already exists");
            }
            partner.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            partner.setDescription(request.getDescription());
        }
        
        if (request.getApiUrl() != null) {
            partner.setApiUrl(request.getApiUrl());
        }
        
        if (request.getAuthMethod() != null) {
            partner.setAuthMethod(AuthMethod.valueOf(request.getAuthMethod().toUpperCase()));
        }
        
        if (request.getConversionRate() != null) {
            partner.setConversionRate(request.getConversionRate());
        }
        
        if (request.getWebhookUrl() != null) {
            partner.setWebhookUrl(request.getWebhookUrl());
        }
        
        if (request.getConnectionTimeout() != null) {
            partner.setConnectionTimeout(request.getConnectionTimeout());
        }
        
        if (request.getReadTimeout() != null) {
            partner.setReadTimeout(request.getReadTimeout());
        }
        
        partner = partnerRepository.save(partner);
        
        logger.info("Partner updated successfully: {} (ID: {})", partner.getName(), partner.getId());
        return convertToPartnerResponse(partner);
    }

    /**
     * Toggle partner active status
     */
    public PartnerResponse togglePartnerStatus(Long id) {
        logger.info("Toggling status for partner: {}", id);
        
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        
        Boolean oldStatus = partner.getIsActive();
        partner.setIsActive(!oldStatus);
        partner = partnerRepository.save(partner);
        
        logger.info("Partner {} status changed from {} to {}", 
                   id, oldStatus, partner.getIsActive());
        
        return convertToPartnerResponse(partner);
    }

    /**
     * Delete partner
     */
    public void deletePartner(Long id) {
        logger.info("Deleting partner: {}", id);
        
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found with ID: " + id));
        
        // Check if partner has active conversions
        long activeConversions = conversionLogRepository.countByPartnerId(id);
        if (activeConversions > 0) {
            logger.warn("Cannot delete partner {} - has {} conversion logs", id, activeConversions);
            throw new RuntimeException("Cannot delete partner with existing conversion logs. " +
                                     "Deactivate the partner instead.");
        }
        
        partnerRepository.delete(partner);
        logger.info("Partner deleted successfully: {} (ID: {})", partner.getName(), id);
    }

    /**
     * Get partner statistics
     */
    public PartnerStatsResponse getPartnerStats() {
        logger.debug("Getting partner statistics");
        
        PartnerStatsResponse stats = new PartnerStatsResponse();
        
        stats.setTotalPartners(partnerRepository.count());
        stats.setActivePartners(partnerRepository.countByIsActive(true));
        stats.setInactivePartners(partnerRepository.countByIsActive(false));
        stats.setTotalAmountProcessed(partnerRepository.sumTotalAmountProcessed());
        
        // Calculate total transactions
        List<Partner> partners = partnerRepository.findAll();
        long totalTransactions = partners.stream()
            .mapToLong(p -> p.getTotalTransactions() != null ? p.getTotalTransactions() : 0L)
            .sum();
        stats.setTotalTransactions(totalTransactions);
        
        // Get top partners
        Pageable topPartnerPageable = PageRequest.of(0, 1);
        
        Page<Partner> topByTransactions = partnerRepository.findTopPartnersByTransactions(topPartnerPageable);
        if (!topByTransactions.isEmpty()) {
            stats.setTopPartnerByTransactions(topByTransactions.getContent().get(0).getName());
        }
        
        Page<Partner> topByAmount = partnerRepository.findTopPartnersByAmount(topPartnerPageable);
        if (!topByAmount.isEmpty()) {
            stats.setTopPartnerByAmount(topByAmount.getContent().get(0).getName());
        }
        
        return stats;
    }

    /**
     * Get active partners for dropdown/selection
     */
    public List<PartnerResponse> getActivePartners() {
        logger.debug("Getting active partners");
        
        return partnerRepository.findByIsActiveTrue()
            .stream()
            .map(this::convertToPartnerResponse)
            .toList();
    }

    /**
     * Convert Partner entity to PartnerResponse DTO
     */
    private PartnerResponse convertToPartnerResponse(Partner partner) {
        PartnerResponse response = new PartnerResponse();
        response.setId(partner.getId());
        response.setName(partner.getName());
        response.setDescription(partner.getDescription());
        response.setApiUrl(partner.getApiUrl());
        response.setAuthMethod(partner.getAuthMethod().name());
        response.setConversionRate(partner.getConversionRate());
        response.setIsActive(partner.getIsActive());
        response.setWebhookUrl(partner.getWebhookUrl());
        response.setConnectionTimeout(partner.getConnectionTimeout());
        response.setReadTimeout(partner.getReadTimeout());
        response.setTotalTransactions(partner.getTotalTransactions());
        response.setTotalAmountProcessed(partner.getTotalAmountProcessed());
        response.setLastTransactionAt(partner.getLastTransactionAt());
        response.setCreatedAt(partner.getCreatedAt());
        response.setUpdatedAt(partner.getUpdatedAt());
        return response;
    }

    /**
     * Generate API key for partner
     */
    private String generateApiKey() {
        return "sk_loyalbridge_" + UUID.randomUUID().toString().replace("-", "");
    }
}