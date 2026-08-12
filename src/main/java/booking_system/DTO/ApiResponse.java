package booking_system.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int errorCode;
    private Map<String, String> errors;

    public static <T> ApiResponse success(T data, String message) {
        return new ApiResponse<>(true, message, data, 0, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, 0, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, 0, null);
    }

    public static <T> ApiResponse<T> error(int errorCode, String message) {
        return new ApiResponse<>(false, message, null, errorCode, null);
    }

    public static <T> ApiResponse<T> validationError(Map<String, String> errors) {
        return new ApiResponse<>(false, "validate error", null, 400, errors);
    }

}
