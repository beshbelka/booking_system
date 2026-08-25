package booking_system.scheduler;

import booking_system.entity.Book;
import booking_system.entity.Seat;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.service.BookService;
import booking_system.service.SeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookService bookService;
    private final SeatService seatService;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelExpiredBookings() {
        try {
            LocalTime expiryTime = LocalTime.now().minusMinutes(15);
            List<Book> expiredBooks = bookService.findByStatusAndCreatedAtBefore(BOOK_STATUS.NOT_PAID, expiryTime);
            if (expiredBooks.isEmpty()) {
                return;
            }
            for (Book book : expiredBooks) {
                book.setStatus(BOOK_STATUS.CANCELLED);
                List<Seat> seats = book.getSeats();
                for (Seat seat : seats) {
                    seat.setStatus(SEAT_STATUS.FREE);
                }
                bookService.save(book);
                seatService.saveAll(seats);
            }
        } catch (Exception e) {
            log.error("CANCEL BOOK ERROR: " + e.getMessage());
        }
    }
}
