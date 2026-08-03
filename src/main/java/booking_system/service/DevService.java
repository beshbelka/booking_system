package booking_system.service;

import booking_system.DTO.RegisterRequest;
import booking_system.entity.User;
import booking_system.enums.USER_ROLE;
import booking_system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevService {

    private final AuthService authService;

    @PostConstruct
    public void createUsers() {

        RegisterRequest user = new RegisterRequest(
                "user@email.com",
                "password",
                "username",
                LocalDate.of(2005, 6, 25)
        );
        authService.register(user);

        RegisterRequest admin = new RegisterRequest(
                "admin@email.com",
                "password",
                "adminame",
                LocalDate.of(2005, 6, 25)
        );
        authService.register(admin);

        log.debug("DevService: user and admin are created");

    }
}
