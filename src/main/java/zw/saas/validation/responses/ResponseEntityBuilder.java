package zw.saas.validation.responses;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseEntityBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.success(message, data);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.created(message, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> alreadyExisting(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.duplicate(message, data);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.failed(message, data);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.notFound(message, data);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalServerError(String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.internalServerError(message, data);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> build(HttpStatus status, String message, T data) {
        ApiResponse<T> response = ApiResponseFactory.of(status, message, data);
        return ResponseEntity.status(status).body(response);
    }
}