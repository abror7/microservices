package org.example.transactionservice.handler;


import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.exception.CustomBadRequestException;
import org.example.transactionservice.exception.CustomGeneralException;
import org.example.transactionservice.exception.ResourceNotFoundException;
import org.example.transactionservice.exception.UniqueKeyException;
import org.example.transactionservice.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(UniqueKeyException.class)
    public ResponseEntity<ApiResponse> handleUniqueKeyException(UniqueKeyException ex) {
        return getApiResponseResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFoundException(ResourceNotFoundException ex) {
        return getApiResponseResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ApiResponse> handleNotFoundException(CustomBadRequestException ex) {
        return getApiResponseResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomGeneralException.class)
    public ResponseEntity<ApiResponse> handleNotFoundException(CustomGeneralException ex) {
        return getApiResponseResponseEntity(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<ApiResponse> getApiResponseResponseEntity(RuntimeException ex, HttpStatus httpStatus) {
        ApiResponse error = new ApiResponse();
        error.setSuccess(false);
        error.setMessage(ex.getMessage());
        log.error(ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(error, httpStatus);
    }
}