package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.service.BookService;
import booking_system.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class BookController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private BookService bookService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookRequest bookRequest,
                                                     HttpServletRequest request) {
        try {
            String token = jwtService.extractTokenFromCookies(request);
            if (token == null || token.isEmpty()) throw new RuntimeException("token is null");
            ApiResponse apiResponse = bookService.createBooking(bookRequest, token);
            return ResponseEntity.ok(ApiResponse.success(apiResponse, "created booking"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
}
