package booking_system.DTO;

public record BookingResponse(
        Long id,
        String status,
        String userEmail,
        String seats,
        Long seanceId,
        String movieTitle
) {
}
