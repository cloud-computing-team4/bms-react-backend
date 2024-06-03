package com.backend.bms.exception;

import com.backend.bms.utils.ApiUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiUtils.ApiFail> handleResourceNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiUtils.fail(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiUtils.ApiFail> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiUtils.fail(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ApiUtils.ApiFail> handleDuplicateNameException(DuplicateNameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiUtils.fail(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiUtils.ApiFail> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiUtils.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error: " + ex.getMessage()));
    }
}
