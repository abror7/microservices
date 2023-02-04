package org.example.buildingservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException() {
        super("Object not found!!!");
    }

    public ResourceNotFoundException(String objectName, Integer id) {
        super(String.format("%s with id: %s not found!", objectName, id));
    }
}
