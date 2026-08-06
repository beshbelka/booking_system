package booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class EmailTakenException extends ErrorResponseException {

    private final String detailMessage;

    private static ProblemDetail asProblem(String email) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("email taken");
        problemDetail.setDetail("Пользователь с email: '%s' уже существует".formatted(email));
        problemDetail.setProperty("email", email);
        return problemDetail;
    }

    public EmailTakenException(String email) {
        super(HttpStatus.CONFLICT, asProblem(email), null);
        this.detailMessage = "Пользователь с email: '%s' уже существует".formatted(email);
    }

    @Override
    public String getMessage() {
        return detailMessage;
    }
}
