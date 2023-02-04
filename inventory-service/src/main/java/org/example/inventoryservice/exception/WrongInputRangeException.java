package org.example.inventoryservice.exception;

public class WrongInputRangeException extends RuntimeException {
    public WrongInputRangeException() {
        super("Wrong input range numbers!!!");
    }

    public WrongInputRangeException(String message) {
        super(message);
    }
}
