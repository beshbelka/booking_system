package booking_system.DTO;

public record MovieFinanceRequest(
        Long id,
        String title,
        Long ticketCount,
        float total,
        float averagePrice
) {
}
