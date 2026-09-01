package booking_system.DTO;

import java.time.LocalTime;

public record SeanceResponse(
        Long id,
        LocalTime start_time,
        LocalTime end_time,
        boolean isAvailable,
        float price,
        int seats,
        int bookedSeats,
        boolean cancelled
) {
}
