package com.retail.reward.controller;

import com.retail.reward.model.RewardSummary;
import com.retail.reward.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private static final Logger log = LoggerFactory.getLogger(RewardController.class);

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public ResponseEntity<List<RewardSummary>> getAllRewards() {
        log.info("Fetching rewards for all customers");
        return ResponseEntity.ok(rewardService.calculateAllRewards());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getCustomerRewards(@PathVariable Long customerId) {
        log.info("Fetching rewards for customer ID: {}", customerId);
        return ResponseEntity.ok(rewardService.getRewardsByCustomerId(customerId));
    }
}