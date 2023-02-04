package org.example.inventoryservice.exception;


public class UniqueKeyException extends RuntimeException {

    public UniqueKeyException() {
        super("Duplicate key value violates unique constraint");
    }

    public UniqueKeyException(String message) {
        super(message);
    }
}
