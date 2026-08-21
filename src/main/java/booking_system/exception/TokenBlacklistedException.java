package booking_system.exception;

public class TokenBlacklistedException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Токен в чёрном списке";
    private static final int DEFAULT_CODE = 401;

    public TokenBlacklistedException() {
        super(DEFAULT_MESSAGE, DEFAULT_CODE);
    }
}
