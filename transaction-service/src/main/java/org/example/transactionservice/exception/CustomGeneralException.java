package org.example.transactionservice.exception;

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
