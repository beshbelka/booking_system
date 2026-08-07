package booking_system.DTO;

public record PasswordChangeRequest(
        String oldPassword,
        String newPassword) {
}
