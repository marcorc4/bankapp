package com.marco.bankapp.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(double balance, double amount) {
        super("Insufficient funds: balance = $" + balance + ", attempted withdrawal = $" + amount);
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}
