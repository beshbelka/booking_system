package booking_system.service;

import booking_system.DTO.RegisterRequest;
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
}
