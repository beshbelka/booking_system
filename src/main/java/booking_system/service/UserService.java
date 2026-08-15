package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.ProfileEditRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.exception.UserNotFoundException;
import booking_system.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

    @Transactional
    public ApiResponse changePassword(String token, String oldPass, String newPass) {
        try {
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            String passwordFromDB = user.getPassword();
            if (!passwordEncoder.matches(oldPass, passwordFromDB)) {
                log.info("password does not match: " + passwordFromDB + ", " + oldPass);
                throw new Exception("password does not match");
            }
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
            Map<String, String> data = new HashMap<>();
            data.put("accessToken", token);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public ApiResponse deleteAccount(String email) {
        try {
            User user = findByEmail(email);
            userRepository.delete(user);
            return ApiResponse.success("delete account ok");
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
