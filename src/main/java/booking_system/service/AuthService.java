package booking_system.service;

import booking_system.DTO.AuthResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.exception.EmailTakenException;
import booking_system.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailTakenException(request.email());
        }

        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.name(), request.birthDate());
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("birthDate", user.getBirthDate()!=null ? user.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "Дата рождения не указана");
        claims.put("role", user.getRole().name());

        return new AuthResponse(jwtService.generateToken(claims, user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("birthDate", user.getBirthDate()!=null ? user.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "Дата рождения не указана");
        claims.put("role", user.getRole().name());
        return new AuthResponse(jwtService.generateToken(claims, user));
    }

    public void logout(String token, HttpServletResponse response) {
        if (token != null) {
            blacklistService.blackList(token);
        }

        Cookie cookie = new Cookie("accessToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);

        log.info("cookie deleted");
    }
}
