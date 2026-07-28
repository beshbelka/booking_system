package booking_system.service;

import booking_system.DTO.UserRegistrationDto;
import booking_system.entity.User;
import booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(UserRegistrationDto userData) {
        if (userRepository.existsByEmail(userData.getEmail())) {
            throw new RuntimeException("Пользователь с email " + userData.getEmail() + " уже существует");
        }

        String encodedPassword = passwordEncoder.encode(userData.getPassword());
        User user = new User(
                userData.getEmail(),
                encodedPassword,
                userData.getName(),
                userData.getBirthDate());

        return userRepository.save(user);
    }
}
