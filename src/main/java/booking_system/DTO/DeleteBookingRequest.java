package booking_system.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeleteBookingRequest(
        @NotBlank(message = "ID бронирования не может быть пустым")
        @Pattern(regexp = "\\d+", message = "ID бронирования должно быть натуральным числом")
        String bookId) {
}
