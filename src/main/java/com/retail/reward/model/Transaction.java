package com.retail.reward.model;

import java.time.LocalDate;

/**
 * Represents a customer's purchase transaction.
 * * @param id The unique identifier for the transaction.
 *
 * @param customerId      The unique identifier of the customer making the purchase.
 * @param amount          The total transaction amount in dollars.
 * @param transactionDate The date the transaction occurred.
 */
public record Transaction(Long id, Long customerId, Double amount, LocalDate transactionDate) {
}