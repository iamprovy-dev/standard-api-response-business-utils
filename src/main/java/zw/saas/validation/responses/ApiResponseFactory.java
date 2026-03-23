package zw.saas.validation.responses;


import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class ApiResponseFactory {

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.CREATED.value())
                .message(message)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> failed(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .success(false)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
                .success(false)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> internalServerError(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .success(false)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> duplicate(String message, T data) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.CONFLICT.value())
                .message(message)
                .success(false)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .success(status.is2xxSuccessful())
                .data(data)
                .build();
    }
}
