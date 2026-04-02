package com.retail.reward.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RetailRewardsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullFlow_ValidCustomer_ShouldReturnCalculatedRewardsFromDatabase() throws Exception {
        // This test relies entirely on the data inserted via data.sql for customer 101
        // Math for 101 in data.sql: $120.50 (90 pts) + $80.00 (30 pts) + $150.00 (150 pts) = 270 total

        mockMvc.perform(get("/api/rewards/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.totalRewardAmount").value(270.00))
                .andExpect(jsonPath("$.monthlyRewardAmount").isMap());
    }

    @Test
    void fullFlow_InvalidCustomer_ShouldReturn404Error() throws Exception {
        mockMvc.perform(get("/api/rewards/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No transactions found for customer ID: 999"));
    }

    @Test
    void fullFlow_GetAllCustomers_ShouldReturnArrayFromDatabase() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Verifies multiple customers are retrieved
                .andExpect(jsonPath("$.length()").value(3));
    }
}