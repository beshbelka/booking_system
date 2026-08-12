package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private BookService bookService;

    public ApiResponse cancelBook(Long bookId) {
        try {
            ApiResponse response = bookService.deleteBooking(bookId);
            if (response.isSuccess()) {
                return ApiResponse.success("Бронь отменена");
            }
            return ApiResponse.error(500, response.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "cancelBook error: " + e.getMessage());
        }
    }

    public ApiResponse payBooking(Long bookId) {
        try {
            ApiResponse response = bookService.setStatusPaid(bookId);
            if (response.isSuccess()) {
                return ApiResponse.success("Бронь оплачена");
            }
            return ApiResponse.error(500, response.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
