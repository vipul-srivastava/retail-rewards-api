package com.retail.reward.service;

import com.retail.reward.entity.TransactionEntity;
import com.retail.reward.exception.CustomerNotFoundException;
import com.retail.reward.model.RewardSummary;
import com.retail.reward.model.Transaction;
import com.retail.reward.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private final TransactionRepository transactionRepository;

    @Value("${reward.threshold.tier1}")
    private BigDecimal tier1Limit;

    @Value("${reward.threshold.tier2}")
    private BigDecimal tier2Limit;

    @Value("${reward.multiplier.tier2}")
    private BigDecimal tier2Multiplier;

    public RewardService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<RewardSummary> calculateAllRewards() {
        List<Transaction> allTransactions = transactionRepository.findAll().stream()
                .map(this::mapToDomain)
                .toList();

        Map<Long, List<Transaction>> transactionsByCustomer = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::customerId));

        return transactionsByCustomer.entrySet().stream()
                .map(entry -> calculateRewardsForCustomer(entry.getKey(), entry.getValue()))
                .toList();
    }

    public RewardSummary getRewardsByCustomerId(Long customerId) {
        List<TransactionEntity> entities = transactionRepository.findByCustomerId(customerId);

        if (entities.isEmpty()) {
            throw new CustomerNotFoundException("No transactions found for customer ID: " + customerId);
        }

        List<Transaction> transactions = entities.stream()
                .map(this::mapToDomain)
                .toList();

        return calculateRewardsForCustomer(customerId, transactions);
    }

    private RewardSummary calculateRewardsForCustomer(Long customerId, List<Transaction> customerTransactions) {
        Map<String, BigDecimal> monthlyRewardAmount = new HashMap<>();
        BigDecimal totalRewardAmount = BigDecimal.ZERO;

        for (Transaction transaction : customerTransactions) {
            BigDecimal rewardAmount = calculateRewardAmount(transaction.amount());

            if (rewardAmount.compareTo(BigDecimal.ZERO) > 0) {
                String monthKey = YearMonth.from(transaction.transactionDate()).toString();

                BigDecimal currentMonthTotal = monthlyRewardAmount.getOrDefault(monthKey, BigDecimal.ZERO);
                monthlyRewardAmount.put(monthKey, currentMonthTotal.add(rewardAmount));

                totalRewardAmount = totalRewardAmount.add(rewardAmount);
            }
        }
        return new RewardSummary(customerId, monthlyRewardAmount, totalRewardAmount);
    }

    public BigDecimal calculateRewardAmount(BigDecimal transactionAmount) {
        if (transactionAmount == null || transactionAmount.compareTo(tier1Limit) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal reward = BigDecimal.ZERO;
        BigDecimal calculableAmount = transactionAmount.setScale(0, RoundingMode.DOWN);

        if (calculableAmount.compareTo(tier2Limit) > 0) {
            BigDecimal overTier2 = calculableAmount.subtract(tier2Limit);
            reward = reward.add(overTier2.multiply(tier2Multiplier));

            BigDecimal tier1MaxAmount = tier2Limit.subtract(tier1Limit);
            reward = reward.add(tier1MaxAmount);
        } else {
            BigDecimal overTier1 = calculableAmount.subtract(tier1Limit);
            reward = reward.add(overTier1);
        }

        return reward;
    }

    private Transaction mapToDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAmount(),
                entity.getTransactionDate()
        );
    }
}