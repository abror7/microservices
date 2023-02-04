package org.example.inventoryservice.exception;


public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException() {
        super("Object not found!!!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String objectName, Integer id) {
        super(String.format("%s with id: %s not found!", objectName, id));
    }
}
