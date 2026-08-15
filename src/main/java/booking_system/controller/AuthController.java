package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.service.AuthService;
import booking_system.service.JwtService;
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
            ApiResponse apiResponse= authService.register(request);
            if (apiResponse.isSuccess()) {
                jwtService.addTokenCookie(response, apiResponse.getData().get("accessToken"));
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
                String token = apiResponse.getData().get("accessToken");
                jwtService.addTokenCookie(response, token);
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
            String token = jwtService.extractTokenFromCookies(request);
            ApiResponse apiResponse = authService.logout(token, response);
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

}
