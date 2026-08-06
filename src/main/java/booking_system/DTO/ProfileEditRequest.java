package booking_system.DTO;

import java.time.LocalDate;

public record ProfileEditRequest (
        String name,
        LocalDate birthDate) {
}
