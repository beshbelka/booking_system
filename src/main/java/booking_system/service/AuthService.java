package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.enums.USER_ROLE;
import booking_system.exception.*;
import booking_system.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BlacklistService blacklistService;
    private final RefreshTokenService refreshTokenService;

    public ApiResponse register(RegisterRequest request, HttpServletResponse response) {
        try {
            if (userRepository.existsByEmail(request.email())) {
                return ApiResponse.error(409, "Пользователь с таким email уже существует");
            }
            if (request.password().length() < 6) {
                return ApiResponse.error(400, "Длина пароля должна быть не менее 6 символов");
            }
            if (request.name().length() < 2) {
                return ApiResponse.error(400, "Длина имени должна быть не менее 2 символов");
            }
            if (request.birthDate().isAfter(LocalDate.now()) || request.birthDate().isBefore(LocalDate.of(1900, 12, 31))) {
                return ApiResponse.error(400, "Дата рождения должна быть в пределах 1900-12-31 - сегодня");
            }

            User user = new User(
                    request.email(),
                    passwordEncoder.encode(request.password()),
                    request.name(),
                    request.birthDate());
            userRepository.save(user);

            String accessToken = jwtService.generateAccessToken(request.email(), USER_ROLE.USER);
            String refreshToken = jwtService.generateRefreshToken(request.email());
            jwtService.addAccessTokenCookie(response, accessToken);
            jwtService.addRefreshTokenCookie(response, refreshToken);

            return ApiResponse.success("register user ok");

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
                HashMap<String, String> token = new HashMap<>();
                token.put("accessToken", jwtService.generateAccessToken(user.getEmail(), user.getRole()));
                token.put("refreshToken", jwtService.generateRefreshToken(user.getEmail()));
                return ApiResponse.success(token);
            }
            throw new UserNotFoundException();
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
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

    public ApiResponse logout(String accessToken, String refreshToken, HttpServletResponse response) {
        try {
            if (accessToken == null || refreshToken == null) {
                throw new TokenIsNullException();
            }
            blacklistService.blackList(accessToken);
            blacklistService.blackList(refreshToken);

            jwtService.clearAccessTokenCookie(response);
            jwtService.clearRefreshTokenCookie(response);

            return ApiResponse.success("logout ok");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public boolean isAuthenticated(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accessToken = jwtService.extractAccessTokenFromCookies(request);
            if (accessToken != null && jwtService.isTokenValid(accessToken)) {
                return true;
            }
            String refreshToken = jwtService.extractRefreshTokenFromCookies(request);
            if (refreshToken != null && jwtService.isRefreshTokenValid(refreshToken)) {
                refreshTokenService.refresh(refreshToken, response, request);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
