package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.entity.*;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import booking_system.exception.BaseException;
import booking_system.exception.BookNotFoundException;
import booking_system.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final SeanceService seanceService;
    private final SeatService seatService;

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
            seatService.save(seat);

            HashMap<String, String> response = new HashMap<>();
            response.put("bookId", book.getId().toString());

            return ApiResponse.success(response);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public Book findById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
    }

    public ApiResponse deleteBooking(Long bookId) {
        try {
            Book book = findById(bookId);
            bookRepository.delete(book);
            return ApiResponse.success("delete booking ok");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @Transactional
    public ApiResponse setStatusPaid(Long bookId) {
        try {
            Book book = findById(bookId);
            book.setStatus(BOOK_STATUS.PAID);
            bookRepository.save(book);
            return ApiResponse.success("set status PAID ok");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
