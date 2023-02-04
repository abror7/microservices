package org.example.transactionservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {


    public NotFoundException() {
        super("Object not found!!!");
    }

    public NotFoundException(String objectName, Integer id) {
        super(String.format("%s with id: %s not found!", objectName, id));
    }
}
