package org.example.buildingservice.exception;

public class CustomGeneralException extends RuntimeException {

    public CustomGeneralException() {
        super("Something went wrong!");
    }

    public CustomGeneralException(String message) {
        super(message);
    }

    public CustomGeneralException(Throwable e) {
        super("Something went wrong!", e.getCause());
    }
}
