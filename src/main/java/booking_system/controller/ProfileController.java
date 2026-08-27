package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.DeleteBookingRequest;
import booking_system.DTO.PasswordChangeRequest;
import booking_system.DTO.ProfileEditRequest;
import booking_system.entity.User;
import booking_system.enums.USER_ROLE;
import booking_system.exception.TokenIsNullException;
import booking_system.service.*;
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
    private final DeleteService deleteService;

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
            String email = jwtService.extractEmail(accessToken);
            User user = userService.findByEmail(email);
            if (user.getRole().equals(USER_ROLE.ADMIN)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "Удалить администратора можно только прямым обращением к БД"));
            }
            String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
            authService.logout(accessToken, refreshToken, response);
            ApiResponse apiResponse = deleteService.deleteAccount(email);
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