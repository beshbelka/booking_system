package booking_system.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private HashMap<String, String> data;

    @Min(value = 100, message = "Код ошибки не может быть меньше 100")
    @Max(value = 500, message = "Код ошибки не может быть больше 500")
    private int code;

    private Map<String, String> errors;

    public static ApiResponse success(HashMap<String, String> data, String message) {
        return new ApiResponse(true, message, data, 200, null);
    }

    public static ApiResponse success(HashMap<String, String> data) {
        return new ApiResponse(true, null, data, 200, null);
    }

    public static  ApiResponse success(String message) {
        return new ApiResponse(true, message, new HashMap<>(), 200, null);
    }

    public static  ApiResponse error(int code, String message) {
        return new ApiResponse(false, message, new HashMap<>(), code, null);
    }

    public static  ApiResponse validationError(Map<String, String> errors) {
        return new ApiResponse(false, "validate error", new HashMap<>(), 400, errors);
    }

    public static ApiResponse error() {
        return new ApiResponse(false, "Внутренняя ошибка сервера", new HashMap<>(), 500, null);
    }

}
