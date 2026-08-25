package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.DTO.BookRequest;
import booking_system.entity.*;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.enums.SEAT_TYPE;
import booking_system.exception.*;
import booking_system.repository.BookRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.ObjectReadContext;

import java.time.LocalTime;
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
            Hall hall = seance.getHall();

            List<Seat> requestedSeats = request.seats();

            if (requestedSeats == null || requestedSeats.isEmpty()) {
                throw new SeatsIsNullException();
            }

            List<Seat> seatsToBook = new ArrayList<>();

            for (Seat seatRequest : requestedSeats) {
                Seat existingSeat = seatService.findByHallRowNumberAndSeance(
                        hall.getId(),
                        seatRequest.getRow(),
                        seatRequest.getNumber(),
                        seance.getId()
                );

                if (existingSeat.getStatus() != SEAT_STATUS.FREE) {
                    throw new SeatIsTakenException(existingSeat.getRow(), existingSeat.getNumber());
                }

                existingSeat.setStatus(SEAT_STATUS.BLOCKED);
                seatsToBook.add(existingSeat);
            }

            seatService.saveAll(seatsToBook);

            Book book = new Book(
                    BOOK_STATUS.NOT_PAID,
                    user,
                    seatsToBook,
                    seance
            );

            book = bookRepository.save(book);

            HashMap<String, String> response = new HashMap<>();
            response.put("bookId", book.getId().toString());

            return ApiResponse.success(response);

        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при создании бронирования: ", e);
            return ApiResponse.error(500, "Внутренняя ошибка сервера");
        }
    }

    public Book findById(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(BookNotFoundException::new);
    }

    public ApiResponse deleteBooking(String email, Long id) {
        try {
            User user = userService.findByEmail(email);
            Book book = findById(id);
            if (!user.getBooks().contains(book)) {
                throw new UnauthorizedException();
            }
            book.setStatus(BOOK_STATUS.CANCELLED);

            List<Seat> seats = book.getSeats();
            seats.forEach(seat -> seat.setStatus(SEAT_STATUS.FREE));
            seatService.saveAll(seats);

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
            List<Seat> seats = book.getSeats();
            for (Seat seat : seats) {
                seat.setStatus(SEAT_STATUS.BOOK);
            }
            seatService.saveAll(seats);
            return ApiResponse.success("set status PAID ok");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public void save(Book book) {
        try {
            bookRepository.save(book);
        } catch (Exception e) {
            log.error("SAVE BOOK ERROR: " + e.getMessage());
        }
    }

    public List<Book> findByStatusAndCreatedAtBefore(BOOK_STATUS bookStatus, LocalTime expiryTime) {
        try {
            return bookRepository.findByStatusAndCreatedAtBefore(bookStatus, expiryTime);
        } catch (Exception e) {
            log.error("FIND BY STATUS AND TIME ERROR: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
