package com.mns.asyncscale.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mns.asyncscale.dto.ErrorResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // handler to send custom error message 
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomException(CustomException ex) {

        log.warn("Server Error: {}", ex.getMessage());

        ErrorResponseDTO response = new ErrorResponseDTO(
                LocalDateTime.now(),
                "Server Error",
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // handler for malformed api request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {

        log.warn("Validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponseDTO response = new ErrorResponseDTO(
                LocalDateTime.now(),
                "Validation Failed",
                "Invalid input parameters",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // handler for malformed endpoints
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        ErrorResponseDTO response = new ErrorResponseDTO(
                LocalDateTime.now(),
                "Bad Request",
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateLimitExceeded(RateLimitExceededException ex) {

        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponseDTO response = new ErrorResponseDTO(
                LocalDateTime.now(),
                "Too Many Requests",
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    // default error handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {

         log.error("Unhandled exception occurred", ex);

        ErrorResponseDTO response = new ErrorResponseDTO(
                LocalDateTime.now(),
                "Internal Server Error",
                null,
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}