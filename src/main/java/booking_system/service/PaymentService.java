package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookService bookService;
    private final SeatService seatService;

    public ApiResponse payBooking(Long bookId) {
        try {
            ApiResponse response = bookService.setStatusPaid(bookId);
            if (response.isSuccess()) {
                return ApiResponse.success("Бронь оплачена");
            }
            return ApiResponse.error(response.getCode(), response.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
