package booking_system.DTO;

import java.time.LocalTime;

public record EditSeanceRequest(
        Long seanceId,
        LocalTime start_time
) {
}
