package booking_system.controller;

import booking_system.DTO.AuthResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.service.AuthService;
import booking_system.service.JwtService;
import booking_system.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/auth/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.register(request);
            addTokenCookie(response, authResponse.accessToken());
            log.debug("AuthController: register success");
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Регистрация провалена: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.login(request);
            addTokenCookie(response, authResponse.accessToken());
            log.info("Вход успешен: " + request.email());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Вход провален: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(900000);
        response.addCookie(cookie);
        log.debug("AuthController: " + cookie.getName() + " saved");
    }
}
