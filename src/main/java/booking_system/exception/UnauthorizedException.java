package booking_system.exception;

public class UnauthorizedException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Недостаточно прав для выполнения действия";
    private static final int DEFAULT_ERROR_CODE = 403;

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE, DEFAULT_ERROR_CODE);
    }
}
