package com.loyalbridge.admin.service;

import com.loyalbridge.admin.model.*;
import com.loyalbridge.admin.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Service for creating sample data for development and testing
 * 
 * @author LoyalBridge Development Team
 * @version 1.0.0
 */
@Service
@Transactional
public class SampleDataService {

    private static final Logger logger = LoggerFactory.getLogger(SampleDataService.class);

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final PointsHistoryRepository pointsHistoryRepository;
    private final ConversionLogRepository conversionLogRepository;

    public SampleDataService(UserRepository userRepository,
                           PartnerRepository partnerRepository,
                           PointsHistoryRepository pointsHistoryRepository,
                           ConversionLogRepository conversionLogRepository) {
        this.userRepository = userRepository;
        this.partnerRepository = partnerRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
        this.conversionLogRepository = conversionLogRepository;
    }

    /**
     * Create sample data after application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createSampleData() {
        if (userRepository.count() == 0) {
            logger.info("Creating sample data for development...");
            createSampleUsers();
            createSamplePartners();
            createSamplePointsHistory();
            createSampleConversionLogs();
            logger.info("Sample data creation completed");
        } else {
            logger.debug("Sample data already exists, skipping creation");
        }
    }

    /**
     * Create sample users
     */
    private void createSampleUsers() {
        String[] names = {
            "John Doe", "Jane Smith", "Bob Johnson", "Alice Brown", "Charlie Wilson",
            "Diana Prince", "Peter Parker", "Mary Watson", "David Clark", "Sarah Connor",
            "Tom Hardy", "Emma Stone", "James Bond", "Natasha Romanoff", "Bruce Wayne"
        };

        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "hotmail.com"};
        UserStatus[] statuses = UserStatus.values();
        Random random = new Random();

        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String email = name.toLowerCase().replace(" ", ".") + "@" + domains[random.nextInt(domains.length)];
            String phone = "+1" + String.format("%010d", 2000000000L + random.nextInt(1000000000));
            
            User user = new User(name, email, phone);
            user.setStatus(statuses[random.nextInt(statuses.length)]);
            user.setTotalPoints(BigDecimal.valueOf(random.nextInt(5000) + 100));
            user.setLifetimeEarnings(BigDecimal.valueOf(random.nextInt(10000) + 500));
            user.setLifetimeRedemptions(BigDecimal.valueOf(random.nextInt(3000)));
            user.setIsHighRisk(random.nextBoolean() && random.nextInt(10) < 2); // 20% chance
            user.setIsVerified(random.nextBoolean() || random.nextInt(10) < 7); // 70% chance
            user.setLastActivity(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            userRepository.save(user);
            logger.debug("Created sample user: {}", user.getName());
        }
    }

    /**
     * Create sample partners
     */
    private void createSamplePartners() {
        Object[][] partnerData = {
            {"Amazon Rewards", "https://api.amazon.com/rewards", AuthMethod.API_KEY, new BigDecimal("0.0100")},
            {"Starbucks Plus", "https://api.starbucks.com/loyalty", AuthMethod.OAUTH2, new BigDecimal("0.0050")},
            {"Uber Credits", "https://api.uber.com/credits", AuthMethod.JWT_TOKEN, new BigDecimal("0.0075")},
            {"Netflix Premium", "https://api.netflix.com/subscriptions", AuthMethod.API_KEY, new BigDecimal("0.0120")},
            {"Apple Store", "https://api.apple.com/store", AuthMethod.OAUTH2, new BigDecimal("0.0090")},
            {"Google Play", "https://api.google.com/play", AuthMethod.JWT_TOKEN, new BigDecimal("0.0080")},
            {"PayPal Cash", "https://api.paypal.com/cash", AuthMethod.BASIC_AUTH, new BigDecimal("0.0110")}
        };

        Random random = new Random();
        
        for (Object[] data : partnerData) {
            Partner partner = new Partner(
                (String) data[0],
                (String) data[1],
                (AuthMethod) data[2],
                (BigDecimal) data[3]
            );
            
            partner.setDescription("Loyalty integration with " + data[0]);
            partner.setIsActive(random.nextBoolean() || random.nextInt(10) < 8); // 80% active
            partner.setApiKey("sk_" + generateRandomString(32));
            partner.setWebhookUrl(data[1] + "/webhook");
            partner.setTotalTransactions((long) random.nextInt(1000));
            partner.setTotalAmountProcessed(BigDecimal.valueOf(random.nextInt(50000) + 1000));
            
            if (random.nextBoolean()) {
                partner.setLastTransactionAt(LocalDateTime.now().minusDays(random.nextInt(7)));
            }
            
            partnerRepository.save(partner);
            logger.debug("Created sample partner: {}", partner.getName());
        }
    }

    /**
     * Create sample points history
     */
    private void createSamplePointsHistory() {
        List<User> users = userRepository.findAll();
        List<Partner> partners = partnerRepository.findAll();
        Random random = new Random();
        TransactionType[] types = TransactionType.values();

        for (User user : users) {
            int transactionCount = random.nextInt(10) + 5; // 5-15 transactions per user
            BigDecimal balance = BigDecimal.ZERO;

            for (int i = 0; i < transactionCount; i++) {
                TransactionType type = types[random.nextInt(types.length)];
                BigDecimal amount = BigDecimal.valueOf(random.nextInt(500) + 10);
                
                // Adjust balance based on transaction type
                if (type == TransactionType.EARN || type == TransactionType.BONUS) {
                    balance = balance.add(amount);
                } else if (type == TransactionType.REDEEM && balance.compareTo(amount) >= 0) {
                    balance = balance.subtract(amount);
                    amount = amount.negate();
                } else {
                    continue; // Skip if not enough balance for redemption
                }

                PointsHistory history = new PointsHistory(
                    user.getId(),
                    type,
                    amount,
                    balance,
                    getTransactionDescription(type, amount)
                );

                if (!partners.isEmpty() && random.nextBoolean()) {
                    history.setPartnerId(partners.get(random.nextInt(partners.size())).getId());
                }

                history.setReferenceId("TXN" + System.currentTimeMillis() + random.nextInt(1000));
                pointsHistoryRepository.save(history);
            }

            // Update user's total points to match the final balance
            user.setTotalPoints(balance);
            userRepository.save(user);
        }
    }

    /**
     * Create sample conversion logs
     */
    private void createSampleConversionLogs() {
        List<User> users = userRepository.findAll();
        List<Partner> partners = partnerRepository.findAll();
        Random random = new Random();
        TransactionType[] types = {TransactionType.REDEEM, TransactionType.EARN};
        ConversionStatus[] statuses = ConversionStatus.values();

        for (int i = 0; i < 50; i++) { // Create 50 conversion logs
            if (users.isEmpty() || partners.isEmpty()) break;

            User user = users.get(random.nextInt(users.size()));
            Partner partner = partners.get(random.nextInt(partners.size()));
            TransactionType type = types[random.nextInt(types.length)];
            
            BigDecimal pointsAmount = BigDecimal.valueOf(random.nextInt(1000) + 50);
            BigDecimal convertedAmount = pointsAmount.multiply(partner.getConversionRate());

            ConversionLog log = new ConversionLog(
                user.getId(),
                partner.getId(),
                type,
                pointsAmount,
                convertedAmount
            );

            log.setConversionRate(partner.getConversionRate());
            log.setStatus(statuses[random.nextInt(statuses.length)]);
            log.setReferenceId("CONV" + System.currentTimeMillis() + random.nextInt(1000));
            log.setDescription(type.name() + " points conversion with " + partner.getName());
            log.setPartnerTransactionId("PTR" + generateRandomString(16));

            if (log.getStatus() == ConversionStatus.COMPLETED) {
                log.setCompletedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            } else if (log.getStatus() == ConversionStatus.FAILED) {
                log.setErrorMessage("Partner API returned error code " + (400 + random.nextInt(100)));
            }

            conversionLogRepository.save(log);
        }
    }

    /**
     * Generate transaction description
     */
    private String getTransactionDescription(TransactionType type, BigDecimal amount) {
        switch (type) {
            case EARN:
                return "Points earned from purchase";
            case REDEEM:
                return "Points redeemed for rewards";
            case BONUS:
                return "Bonus points awarded";
            case TRANSFER:
                return "Points transferred";
            case ADJUSTMENT:
                return "Admin adjustment";
            case EXPIRY:
                return "Points expired";
            default:
                return "Transaction processed";
        }
    }

    /**
     * Generate random string
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}