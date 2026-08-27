package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.exception.BaseException;
import booking_system.exception.TokenBlacklistedException;
import booking_system.exception.TokenIsNullException;
import booking_system.service.AuthService;
import booking_system.service.JwtService;
import booking_system.service.BlacklistService;
import booking_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            ApiResponse apiResponse= authService.register(request, response);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            ApiResponse apiResponse = authService.login(request);
            if (apiResponse.isSuccess()) {
                jwtService.addAccessTokenCookie(response, apiResponse.getData().get("accessToken"));
                jwtService.addRefreshTokenCookie(response, apiResponse.getData().get("refreshToken"));
                return ResponseEntity.ok(ApiResponse.success(apiResponse.getData()));
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = jwtService.extractAccessTokenFromCookies(request);
            String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
            ApiResponse apiResponse = authService.logout(accessToken, refreshToken, response);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        try {
            String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
            ApiResponse apiResponse = authService.refresh(refreshToken, response);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok().body(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

}
