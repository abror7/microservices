package org.example.buildingservice.exception;

public class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException() {
        super("Bad request!!");
    }

    public CustomBadRequestException(String message) {
        super(message);
    }
}
