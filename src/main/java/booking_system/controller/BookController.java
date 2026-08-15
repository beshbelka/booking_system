package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.exception.BaseException;
import booking_system.service.BookService;
import booking_system.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookController {

    private final JwtService jwtService;
    private final BookService bookService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookRequest bookRequest,
                                                     HttpServletRequest request) {
        try {
            String token = jwtService.extractTokenFromCookies(request);
            if (token == null || token.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "token is null or empty"));
            } else if (!jwtService.isTokenValid(token)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "token invalid"));
            }
            ApiResponse apiResponse = bookService.createBooking(bookRequest, token);
            return ResponseEntity.ok(apiResponse);
        } catch (BaseException e) {
            return ResponseEntity
                    .status(e.getErrorCode())
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }
}
