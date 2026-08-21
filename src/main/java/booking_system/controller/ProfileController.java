package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.DeleteBookingRequest;
import booking_system.DTO.PasswordChangeRequest;
import booking_system.DTO.ProfileEditRequest;
import booking_system.exception.TokenIsNullException;
import booking_system.service.AuthService;
import booking_system.service.BookService;
import booking_system.service.JwtService;
import booking_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final BookService bookService;

    @PostMapping("/edit")
    public ResponseEntity<ApiResponse> editProfile(
            @Valid @RequestBody ProfileEditRequest data,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String token = jwtService.extractAccessTokenFromCookies(request);
            if (token == null || token.isEmpty()) throw new TokenIsNullException();
            ApiResponse apiResponse = userService.editProfile(data, token);
            if (apiResponse.isSuccess()) {
                HashMap<String, String> responseData = apiResponse.getData();
                String newToken = responseData.get("accessToken");
                jwtService.addAccessTokenCookie(response, newToken);
                return ResponseEntity.ok(ApiResponse.success("edit ok"));
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest,
            HttpServletRequest request) {
        try {
            String oldPass = passwordChangeRequest.oldPassword();
            String newPass = passwordChangeRequest.newPassword();
            String token = jwtService.extractAccessTokenFromCookies(request);
            ApiResponse apiResponse = userService.changePassword(token, oldPass, newPass);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("changePassword ok"));
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<ApiResponse> deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = jwtService.extractAccessTokenFromCookies(request);
            String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
            authService.logout(accessToken, refreshToken, response);
            String email = jwtService.extractEmail(accessToken);
            ApiResponse apiResponse = userService.deleteAccount(email);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok().body(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }

    @DeleteMapping("/deleteBooking")
    public ResponseEntity<ApiResponse> deleteBooking(
            HttpServletRequest request,
            @Valid @RequestBody DeleteBookingRequest deleteBookingRequest
    ) {
        try {
            String bookIdString = deleteBookingRequest.bookId();
            if (bookIdString == null || !bookIdString.matches("\\d+")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "Неверный ID бронирования"));
            }
            String token = jwtService.extractAccessTokenFromCookies(request);
            String email = jwtService.extractEmail(token);
            Long id = Long.parseLong(bookIdString);
            ApiResponse apiResponse = bookService.deleteBooking(email, id);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok().body(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }
}