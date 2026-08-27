package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.ProfileEditRequest;
import booking_system.entity.Book;
import booking_system.entity.Seat;
import booking_system.entity.User;
import booking_system.enums.USER_ROLE;
import booking_system.exception.BaseException;
import booking_system.exception.PasswordDoesNotMatchException;
import booking_system.exception.UserNotFoundException;
import booking_system.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SeatService seatService;
    private final DeleteService deleteService;

    @Transactional
    public ApiResponse editProfile(@Valid ProfileEditRequest data, String token) {
        try {
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            String name = data.name();
            LocalDate birthDate = data.birthDate();

            if (name != null && !name.isEmpty()) {
                user.setName(name);
            }

            if (birthDate != null) {
                user.setBirthDate(birthDate);
            }

            userRepository.save(user);

            String newToken = jwtService.generateAccessToken(email, user.getRole());
            HashMap<String, String> newData = new HashMap<>();
            newData.put("accessToken", newToken);

            return ApiResponse.success(newData);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Transactional
    public ApiResponse changePassword(String token, String oldPass, String newPass) {
        try {
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            String passwordFromDB = user.getPassword();
            if (!passwordEncoder.matches(oldPass, passwordFromDB)) {
                throw new PasswordDoesNotMatchException();
            }
            user.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(user);
            HashMap<String, String> data = new HashMap<>();
            data.put("accessToken", token);
            return ApiResponse.success(data);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public HashMap<String, String> getNameAndBirthDate(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        HashMap<String, String> nameAnsBirthDate = new HashMap<>();
        nameAnsBirthDate.put("name", user.getName());
        nameAnsBirthDate.put("birthDate", user.getBirthDate().toString());
        return nameAnsBirthDate;
    }

    public List<Book> getBooks(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user.getBooks();
    }

    public USER_ROLE getRole(String email) {
        User user = findByEmail(email);
        return user.getRole();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
