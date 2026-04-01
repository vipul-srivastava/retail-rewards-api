package com.retail.reward.controller;

import com.retail.reward.model.RewardSummary;
import com.retail.reward.service.RewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller providing endpoints for the Retail Rewards program.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    /**
     * Injects the RewardService dependency.
     *
     * @param rewardService The service handling reward logic.
     */
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Endpoint to retrieve reward summaries for all customers.
     *
     * @return ResponseEntity containing a list of RewardSummary objects.
     */
    @GetMapping
    public ResponseEntity<List<RewardSummary>> getAllRewards() {
        return ResponseEntity.ok(rewardService.calculateAllRewards());
    }

    /**
     * Endpoint to retrieve the reward summary for a specific customer.
     *
     * @param customerId The ID of the target customer.
     * @return ResponseEntity containing the customer's RewardSummary.
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getCustomerRewards(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardService.getRewardsByCustomerId(customerId));
    }
}
