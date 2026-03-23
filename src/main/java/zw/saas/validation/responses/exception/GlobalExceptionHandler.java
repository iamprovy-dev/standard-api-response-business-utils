package zw.saas.validation.responses.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zw.saas.validation.responses.ApiResponse;
import zw.saas.validation.responses.ResponseEntityBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ?
                    ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage != null ? errorMessage : "Invalid value");
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntityBuilder.badRequest("Validation failed", errors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException exception) {

        log.warn("Validation exception: {}", exception.getMessage());
        return ResponseEntityBuilder.build(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExists(
            AlreadyExistsException exception) {

        log.warn("Resource already exists: {}", exception.getMessage());
        return ResponseEntityBuilder.build(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(DoesNotExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleDoesNotExist(
            DoesNotExistException exception) {

        log.warn("Resource not found: {}", exception.getMessage());
        return ResponseEntityBuilder.build(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException exception) {

        log.warn("Unauthorized access: {}", exception.getMessage());
        return ResponseEntityBuilder.build(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(MissingFieldsException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingFields(
            MissingFieldsException exception) {

        log.warn("Missing fields: {}", exception.getMessage());
        return ResponseEntityBuilder.build(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(RequestFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestFailed(
            RequestFailedException exception) {

        log.error("Request failed: {}", exception.getMessage(), exception);
        return ResponseEntityBuilder.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception exception) {

        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return ResponseEntityBuilder.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }
}
