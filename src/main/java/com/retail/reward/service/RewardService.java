package com.retail.reward.service;

import com.retail.reward.exception.CustomerNotFoundException;
import com.retail.reward.model.RewardSummary;
import com.retail.reward.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for processing transactions and calculating reward points.
 */
@Service
public class RewardService {

    private static final int REWARD_LIMIT_TIER_1 = 50;
    private static final int REWARD_LIMIT_TIER_2 = 100;

    private final List<Transaction> transactions;

    /**
     * Initializes the service with a mock dataset spanning a 3-month period.
     */
    public RewardService() {
        this.transactions = initializeMockData();
    }

    /**
     * Calculates reward points for all customers in the system.
     *
     * @return A list of RewardSummary objects for each customer.
     */
    public List<RewardSummary> calculateAllRewards() {
        Map<Long, List<Transaction>> transactionsByCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::customerId));

        return transactionsByCustomer.entrySet().stream()
                .map(entry -> calculateRewardsForCustomer(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates reward points for a specific customer ID.
     *
     * @param customerId The ID of the customer.
     * @return RewardSummary object containing monthly and total points.
     * @throws CustomerNotFoundException if no transactions exist for the customer.
     */
    public RewardSummary getRewardsByCustomerId(Long customerId) {
        List<Transaction> customerTransactions = transactions.stream()
                .filter(t -> t.customerId().equals(customerId))
                .collect(Collectors.toList());

        if (customerTransactions.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for customer ID: " + customerId);
        }

        return calculateRewardsForCustomer(customerId, customerTransactions);
    }

    /**
     * Core logic to calculate points and group them by month.
     *
     * @param customerId The customer ID.
     * @param customerTransactions The list of transactions for the customer.
     * @return The aggregated RewardSummary.
     */
    private RewardSummary calculateRewardsForCustomer(Long customerId, List<Transaction> customerTransactions) {
        Map<String, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;

        for (Transaction transaction : customerTransactions) {
            int points = calculatePoints(transaction.amount());
            if (points > 0) {
                // Fulfills Requirement: "Do not hard code the months"
                String monthName = transaction.transactionDate().getMonth().name();
                monthlyPoints.put(monthName, monthlyPoints.getOrDefault(monthName, 0) + points);
                totalPoints += points;
            }
        }
        return new RewardSummary(customerId, monthlyPoints, totalPoints);
    }

    /**
     * Applies the mathematical formula for reward points based on the transaction amount.
     *
     * @param amount The transaction amount.
     * @return The calculated reward points.
     */
    public int calculatePoints(Double amount) {
        if (amount == null || amount <= REWARD_LIMIT_TIER_1) {
            return 0;
        }

        int points = 0;
        int truncatedAmount = amount.intValue();

        if (truncatedAmount > REWARD_LIMIT_TIER_2) {
            points += (truncatedAmount - REWARD_LIMIT_TIER_2) * 2;
            points += REWARD_LIMIT_TIER_1;
        } else {
            points += (truncatedAmount - REWARD_LIMIT_TIER_1);
        }

        return points;
    }

    /**
     * Generates a mock dataset demonstrating multiple customers and transactions over 3 months.
     *
     * @return A list of populated transactions.
     */
    private List<Transaction> initializeMockData() {
        LocalDate currentMonth = LocalDate.now();
        LocalDate lastMonth = currentMonth.minusMonths(1);
        LocalDate twoMonthsAgo = currentMonth.minusMonths(2);

        return Arrays.asList(
                // Customer 101 - Multiple transactions across 3 months
                new Transaction(1L, 101L, 120.0, currentMonth),     // 90 points
                new Transaction(2L, 101L, 80.0, currentMonth),      // 30 points
                new Transaction(3L, 101L, 50.0, lastMonth),         // 0 points
                new Transaction(4L, 101L, 150.0, twoMonthsAgo),     // 150 points

                // Customer 102 - Negative scenario setup
                new Transaction(5L, 102L, 40.0, currentMonth),      // 0 points

                // Customer 103 - Exact edge case values
                new Transaction(6L, 103L, 100.0, lastMonth)         // 50 points
        );
    }
}
