package com.retail.reward.controller;

import com.retail.reward.exception.CustomerNotFoundException;
import com.retail.reward.model.RewardSummary;
import com.retail.reward.service.RewardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void testGetCustomerRewards_Success() throws Exception {
        RewardSummary mockSummary = new RewardSummary(
                101L,
                Map.of("2026-04", new BigDecimal("120.00")),
                new BigDecimal("120.00")
        );

        Mockito.when(rewardService.getRewardsByCustomerId(101L)).thenReturn(mockSummary);

        mockMvc.perform(get("/api/rewards/101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.totalRewardAmount").value(120.00));
    }

    @Test
    void testGetCustomerRewards_NotFound() throws Exception {
        Mockito.when(rewardService.getRewardsByCustomerId(999L))
                .thenThrow(new CustomerNotFoundException("No transactions found for customer ID: 999"));

        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No transactions found for customer ID: 999"));
    }
}