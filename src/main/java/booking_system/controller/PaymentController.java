package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.PaymentRequest;
import booking_system.entity.Book;
import booking_system.service.BookService;
import booking_system.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookService bookService;

    @PostMapping("/payment")
    @ResponseBody
    public ResponseEntity<ApiResponse> pay(@Valid @RequestBody PaymentRequest request) {
        try {
            double success = Math.random();
            Long bookId = request.bookId();
            if (success >= 0.5) {
                ApiResponse response = paymentService.payBooking(bookId);
                if (response.isSuccess()) {
                    return ResponseEntity.ok(ApiResponse.success("pay ok"));
                }
                log.error(response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, response.getMessage()));
            }
            ApiResponse response = paymentService.cancelBook(bookId);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                        .body(ApiResponse.error(402, "Оплата не прошла, бронь отменена"));
            }
            log.error(response.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, response.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Ошибка обработки платежа"));
        }
    }

    @GetMapping("/payment")  // ← GET для страницы
    public String showPaymentPage(@RequestParam Long bookId, Model model) {
        // Проверяем, что бронь существует
        Book book = bookService.findById(bookId);
        model.addAttribute("bookingId", bookId);
        model.addAttribute("book", book);
        return "payment";  // payment.html
    }
}
