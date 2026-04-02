package com.retail.reward.service;

import com.retail.reward.entity.TransactionEntity;
import com.retail.reward.exception.CustomerNotFoundException;
import com.retail.reward.model.RewardSummary;
import com.retail.reward.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        // Manually inject the properties since Spring Boot isn't running in this unit test
        ReflectionTestUtils.setField(rewardService, "tier1Limit", new BigDecimal("50"));
        ReflectionTestUtils.setField(rewardService, "tier2Limit", new BigDecimal("100"));
        ReflectionTestUtils.setField(rewardService, "tier2Multiplier", new BigDecimal("2"));
    }

    @Test
    void testCalculateRewardAmount_LogicValidation() {
        // Under $50
        assertEquals(0, BigDecimal.ZERO.compareTo(rewardService.calculateRewardAmount(new BigDecimal("40.00"))));

        // Exactly $50
        assertEquals(0, BigDecimal.ZERO.compareTo(rewardService.calculateRewardAmount(new BigDecimal("50.00"))));

        // Between $50 and $100 ($80 -> 30)
        assertEquals(0, new BigDecimal("30").compareTo(rewardService.calculateRewardAmount(new BigDecimal("80.00"))));

        // Over $100 ($120 -> 90)
        assertEquals(0, new BigDecimal("90").compareTo(rewardService.calculateRewardAmount(new BigDecimal("120.00"))));
    }

    @Test
    void testGetRewardsByCustomerId_Success() {
        // Arrange Mock Data
        TransactionEntity tx1 = new TransactionEntity();
        tx1.setId(1L); tx1.setCustomerId(101L); tx1.setAmount(new BigDecimal("120.00")); tx1.setTransactionDate(LocalDate.now());

        List<TransactionEntity> mockEntities = Arrays.asList(tx1);
        Mockito.when(transactionRepository.findByCustomerId(101L)).thenReturn(mockEntities);

        // Act
        RewardSummary summary = rewardService.getRewardsByCustomerId(101L);

        // Assert
        assertNotNull(summary);
        assertEquals(101L, summary.customerId());
        assertEquals(0, new BigDecimal("90").compareTo(summary.totalRewardAmount()));
    }

    @Test
    void testGetRewardsByCustomerId_ThrowsException() {
        Mockito.when(transactionRepository.findByCustomerId(999L)).thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class, () -> rewardService.getRewardsByCustomerId(999L));
    }
}