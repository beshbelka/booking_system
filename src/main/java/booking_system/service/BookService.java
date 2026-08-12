package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.entity.*;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import booking_system.repository.BookRepository;
import booking_system.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SeanceService seanceService;
    @Autowired
    private SeatRepository seatRepository;

    public long getBookCount() {
        return bookRepository.count();
    }

    @Transactional
    public Book createBooking(BookRequest request, String token) {
        try {

            String email = jwtService.extractEmail(token);
            User user = userService.findByEmail(email);

            Seance seance = seanceService.findById(request.seanceId());

            List<Seat> seats = new ArrayList<>();
            Seat seat = new Seat(
                    request.row(),
                    request.number(),
                    SEAT_STATUS.BOOK,
                    0,
                    SEAT_TYPE.ORDINARY,
                    seance.getHall()
            );
            seats.add(seat);

            Book book = new Book(
                    BOOK_STATUS.NOT_PAID,
                    user,
                    seats,
                    seance
            );

            bookRepository.save(book);
            seatRepository.save(seat);

            return book;
        } catch (Exception e) {
            return new Book();
        }
    }

    public Book findById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("book not found"));
    }

    public ApiResponse deleteBooking(Long bookId) {
        try {
            Book book = findById(bookId);
            bookRepository.delete(book);
            return ApiResponse.success(bookId, "deleteBooking ok");
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public ApiResponse setStatusPaid(Long bookId) {
        try {
            Book book = findById(bookId);
            book.setStatus(BOOK_STATUS.PAID);
            bookRepository.save(book);
            return ApiResponse.success(bookId, "set status PAID ok");
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
