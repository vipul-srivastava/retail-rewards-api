package com.retail.reward.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object representing the calculated reward amounts for a customer.
 */
public record RewardSummary(
        Long customerId,
        Map<String, BigDecimal> monthlyRewardAmount,
        BigDecimal totalRewardAmount
) {
}