package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.entity.*;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import booking_system.repository.BookRepository;
import booking_system.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public ApiResponse createBooking(BookRequest request, String token) {
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

            return ApiResponse.success(book.getId(), "book ok");
        } catch (Exception e) {
            return ApiResponse.error(500, "book fail");
        }
    }
}
