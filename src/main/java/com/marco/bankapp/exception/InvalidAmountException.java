package com.marco.bankapp.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(double amount) {
        super("Invalid amount: " + amount);
    }

    // Agrega este constructor para usar mensajes custom
    public InvalidAmountException(String message) {
        super(message);
    }
}
