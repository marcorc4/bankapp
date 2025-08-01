package com.marco.bankapp.exception;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String operation) {
        super("You are not authorized to perform this operation: " + operation);
    }
}
