package com.retail.reward.service;

import com.retail.reward.exception.CustomerNotFoundException;
import com.retail.reward.model.RewardSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the RewardService core calculation logic.
 */
class RewardServiceTest {

    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        rewardService = new RewardService();
    }

    @Test
    void testCalculatePoints_Over100Dollars() {
        assertEquals(90, rewardService.calculatePoints(120.0));
    }

    @Test
    void testCalculatePoints_Between50And100Dollars() {
        assertEquals(25, rewardService.calculatePoints(75.0));
    }

    @Test
    void testCalculatePoints_Exactly100Dollars() {
        assertEquals(50, rewardService.calculatePoints(100.0));
    }

    @Test
    void testCalculatePoints_NegativeScenario_Under50Dollars() {
        assertEquals(0, rewardService.calculatePoints(40.0));
    }

    @Test
    void testGetRewardsByCustomerId_Success() {
        RewardSummary summary = rewardService.getRewardsByCustomerId(101L);
        assertNotNull(summary);
        assertEquals(101L, summary.customerId());
        assertTrue(summary.totalPoints() > 0);
    }

    @Test
    void testGetRewardsByCustomerId_NegativeScenario_CustomerNotFound() {
        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            rewardService.getRewardsByCustomerId(999L);
        });
        assertTrue(exception.getMessage().contains("No transactions found for customer ID: 999"));
    }
}
