package booking_system.DTO;

import java.time.LocalDate;

public record RegisterRequest(
        String email,
        String password,
        String name,
        LocalDate birthDate) {
}
