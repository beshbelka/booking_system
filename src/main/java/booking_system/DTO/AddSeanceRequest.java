package booking_system.DTO;

import booking_system.entity.Hall;
import booking_system.entity.Movie;

import java.time.LocalTime;

public record AddSeanceRequest(
        String movieTitle,
        Long hallId,
        LocalTime start_time,
        float price
) {
}
