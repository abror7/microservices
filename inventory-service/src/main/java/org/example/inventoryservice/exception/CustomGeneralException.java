package org.example.inventoryservice.exception;

public class CustomGeneralException extends RuntimeException {

    public CustomGeneralException() {
        super("Something went wrong!");
    }

    public CustomGeneralException(String message) {
        super(message);
    }
}
