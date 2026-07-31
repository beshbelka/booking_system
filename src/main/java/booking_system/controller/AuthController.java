package booking_system.controller;

import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.service.AuthService;
import booking_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Регистрация успешна: " + request.email());
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            log.error("Регистрация провалена: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.info("Вход успешен: " + request.email());
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            log.error("Вход провален: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
