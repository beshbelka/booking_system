package booking_system.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationDto {

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotNull(message = "Дата рождения обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Дата должна быть в прошлом")
    private LocalDate birthDate;

    @Size(min = 6, message = "Пароль минимум 6 символов")
    private String password;

}
