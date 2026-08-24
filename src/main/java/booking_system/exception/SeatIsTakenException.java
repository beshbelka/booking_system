package booking_system.exception;

public class SeatIsTakenException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Место (%d, %d) уже занято";
    private static final int DEFAULT_ERROR_CODE = 409;

    public SeatIsTakenException(int row, int number) {
        super(String.format(DEFAULT_MESSAGE, row, number), DEFAULT_ERROR_CODE);
    }
}
