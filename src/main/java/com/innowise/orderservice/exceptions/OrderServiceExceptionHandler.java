package com.innowise.orderservice.exceptions;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class OrderServiceExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(exception = {ItemNotFoundException.class,
            OrderNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AlreadyDeletedException.class)
    public ResponseEntity<String> handleAlreadyDeletedException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ImmutableOrderUpdateException.class)
    public ResponseEntity<String> handleOrderNotFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
