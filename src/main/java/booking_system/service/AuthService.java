package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService blacklistService;

    public ApiResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.email())) {
                return ApiResponse.error(409, "Пользователь с таким email уже существует");
            }

            User user = new User(request.email(), passwordEncoder.encode(request.password()), request.name(), request.birthDate());
            userRepository.save(user);

            Map<String, Object> claims = putClaims(user);

            HashMap<String, String> token = new HashMap<>();
            token.put("accessToken", jwtService.generateToken(claims, user));

            return ApiResponse.success(token);

        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public ApiResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            User user = (User) auth.getPrincipal();
            if (user != null) {
                Map<String, Object> claims = putClaims(user);

                HashMap <String, String> token = new HashMap<>();
                token.put("accessToken", jwtService.generateToken(claims, user));

                return ApiResponse.success(token);
            }
            return ApiResponse.error(401, "Пользователь не авторизован");
        } catch (Exception e) {
            String message;
            if (e instanceof BadCredentialsException) {
                message = "Неверный email или пароль";
            } else {
                message = "Ошибка аутентификации";
                log.error(e.getMessage());
            }
            return ApiResponse.error(401, message);
        }
    }

    public ApiResponse logout(String token, HttpServletResponse response) {
        try {
            if (token == null) {
                return ApiResponse.error(401, "token is null");
            }
            blacklistService.blackList(token);
            Cookie cookie = new Cookie("accessToken", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            response.addCookie(cookie);

            return ApiResponse.success("logout ok");

        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    private Map<String, Object> putClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("birthDate", user.getBirthDate() != null ? user.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "Дата рождения не указана");
        claims.put("role", user.getRole().name());
        return claims;
    }
}
