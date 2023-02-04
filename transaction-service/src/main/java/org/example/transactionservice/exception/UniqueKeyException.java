package org.example.transactionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UniqueKeyException extends RuntimeException {

    public UniqueKeyException() {
        super("Duplicate key value violates unique constraint");
    }

    public UniqueKeyException(String message) {
        super(message);
    }
}
