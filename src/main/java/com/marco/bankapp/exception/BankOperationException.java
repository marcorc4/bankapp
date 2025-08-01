package com.marco.bankapp.exception;

// Excepción base
public class BankOperationException extends RuntimeException {
    public BankOperationException(String message) {
        super(message);
    }

    public BankOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
