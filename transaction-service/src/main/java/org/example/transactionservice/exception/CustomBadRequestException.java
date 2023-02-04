package org.example.transactionservice.exception;

public class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException() {
        super("Bad request!!");
    }

    public CustomBadRequestException(String message) {
        super(message);
    }
}
