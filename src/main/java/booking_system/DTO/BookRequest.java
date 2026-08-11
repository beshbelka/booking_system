package booking_system.DTO;

public record BookRequest(
        Long movieId,
        Long seanceId,
        Short row,
        Short number
) {
}
