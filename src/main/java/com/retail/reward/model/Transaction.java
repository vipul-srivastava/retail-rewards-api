package com.retail.reward.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a customer's purchase transaction in the domain layer.
 */
public record Transaction(Long id, Long customerId, BigDecimal amount, LocalDate transactionDate) {
}