package booking_system.service;

import booking_system.entity.User;
import booking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // зашифровать
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        // зашифровать пароль
        return userRepository.save(user);
    }
}
