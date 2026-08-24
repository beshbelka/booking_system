package booking_system.DTO;

import booking_system.entity.Seat;

import java.util.ArrayList;

public record BookRequest(
        Long seanceId,
        ArrayList<Seat> seats
) {
}
