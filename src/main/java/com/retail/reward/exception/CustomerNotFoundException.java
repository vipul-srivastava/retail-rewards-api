package com.retail.reward.exception;

/**
 * Custom exception thrown when a reward summary is requested for a customer with no transactions.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Constructs a new CustomerNotFoundException.
     * * @param message The detailed error message.
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
