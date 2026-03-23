package zw.saas.validation.responses.exception;//package com.classify.system_settings.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zw.saas.validation.responses.ApiResponse;

import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
//@ControllerAdvice
public class CustomValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(
                ApiResponse.<Map<String, String>>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExisting(
            AlreadyExistsException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.CONFLICT.value())
                        .success(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DoesNotExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleDoesNotExist(
            DoesNotExistException exception
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .success(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> unauthorized(
            UnauthorizedException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .success(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(MissingFieldsException.class)
    public ResponseEntity<ApiResponse<Void>> incomplete(
            MissingFieldsException exception
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .success(false)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(RequestFailedException.class)
    public ResponseEntity<ApiResponse<Void>> requestFailed(
            RequestFailedException exception
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Void>builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .success(false)
                        .message(exception.getMessage())
                        .build()
        );
    }
}
