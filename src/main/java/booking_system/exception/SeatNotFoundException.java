package booking_system.exception;

public class SeatNotFoundException extends BaseException {

    private static final String DEFAULT_MESSAGE = "Место не найдено: %d сеанс, %d зал, %d ряд, %d номер";
    private static final int DEFAULT_ERROR_CODE = 404;

    public SeatNotFoundException(short row, short number, Long hallId, Long seanceId) {
        super(String.format(DEFAULT_MESSAGE, seanceId, hallId, row, number), DEFAULT_ERROR_CODE);
    }
}
