package booking_system.controller;

import booking_system.entity.User;
import booking_system.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = authService.authenticate(email, password);
            if (user != null) {
                session.setAttribute("user", user);
                response.put("success", true);
                response.put("message", "Вход выполнен успешно");
                response.put("user", user.getName());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Неверный email или пароль");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam LocalDate birthDate,
            HttpSession session
            ) {

        Map<String, Object> responce = new HashMap<>();

        try {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            user.setBirthDate(birthDate);

            User savedUser = authService.register(user);
            session.setAttribute("user", savedUser);

            responce.put("success", true);
            responce.put("message", "Регистрация прошла успешно");
            responce.put("user", savedUser.getName());

            return ResponseEntity.ok(responce);
        } catch (Exception e) {
            responce.put("success", false);
            responce.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(responce);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout (HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Выход выполнен успешно");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkSession (HttpSession session) {
        Map<String, Object> responce = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            responce.put("authenticated", true);
            responce.put("user", user.getName());
        } else {
            responce.put("authenticated", false);
        }
        return ResponseEntity.ok(responce);
    }
}
