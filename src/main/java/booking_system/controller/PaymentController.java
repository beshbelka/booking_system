package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.PaymentRequest;
import booking_system.entity.Book;
import booking_system.enums.BOOK_STATUS;
import booking_system.service.BookService;
import booking_system.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookService bookService;

    @PostMapping("/payment")
    @ResponseBody
    public ResponseEntity<ApiResponse> pay(@Valid @RequestBody PaymentRequest request) {
        try {
            Long bookId = request.bookId();
            Book book = bookService.findById(bookId);
            if (book.getStatus().equals(BOOK_STATUS.PAID)) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "Бронь уже оплачена"));
            }
            double success = Math.random();
            if (success <= 0.8) {
                ApiResponse response = paymentService.payBooking(bookId);
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                }
                return ResponseEntity
                        .status(response.getCode())
                        .body(response);
            }
            return ResponseEntity
                    .status(HttpStatus.PAYMENT_REQUIRED)
                    .body(ApiResponse.error(402, "Оплата не прошла"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Ошибка обработки платежа"));
        }
    }
}
