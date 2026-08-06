package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.ProfileEditRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Пользователь с email " + request.email() + " уже существует");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.email(),
                encodedPassword,
                request.name(),
                request.birthDate());

        return userRepository.save(user);
    }

    @Transactional
    public ApiResponse editProfile(@Valid ProfileEditRequest data, String token) {
        try {
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            String name = data.name();
            LocalDate birthDate = data.birthDate();

            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            if (birthDate != null) {
                user.setBirthDate(birthDate);
            }

            userRepository.save(user);

            Map<String, Object> claims = new HashMap<>();
            claims.put("name", name);
            claims.put("birthDate", birthDate);
            claims.put("role", user.getRole());

            String newToken = jwtService.generateToken(claims, user);
            HashMap<String, String> newData = new HashMap<>();
            newData.put("accessToken", newToken);

            return ApiResponse.success(newData);

        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
