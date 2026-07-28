package booking_system.controller;

import booking_system.DTO.UserRegistrationDto;
import booking_system.entity.User;
import booking_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userData) {
        try {
            User registeredUser = userService.register(userData);
            log.info("Регистрация успешна: " + registeredUser.getEmail());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Регистрация успешна"
            ));
        } catch (Exception e) {
            log.error("Регистрация провалена: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
