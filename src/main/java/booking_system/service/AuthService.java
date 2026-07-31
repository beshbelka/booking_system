package booking_system.service;

import booking_system.DTO.AuthResponse;
import booking_system.DTO.LoginRequest;
import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.exception.EmailTakenException;
import booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailTakenException(request.email());
        }

        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.name(), request.birthDate());
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(user));
    }
}
