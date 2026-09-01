package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.*;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.exception.BaseException;
import booking_system.exception.MovieNotFoundException;
import booking_system.exception.SeanceNotFoundException;
import booking_system.exception.UserNotFoundException;
import booking_system.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteService {

    private final BookRepository bookRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final HallService hallService;

    public void deleteAllBooks(List<Book> books) {
        try {
            for (Book book : books) {
                book.setStatus(BOOK_STATUS.CANCELLED);
                book.setUser(null);
            }
            bookRepository.saveAll(books);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public void cancelExpiredBookings() {
        try {
            LocalTime expiryTime = LocalTime.now().minusMinutes(15);
            List<Book> expiredBooks = bookRepository.findByStatusAndCreatedAtBefore(BOOK_STATUS.NOT_PAID, expiryTime);
            if (expiredBooks.isEmpty()) {
                return;
            }
            for (Book book : expiredBooks) {
                book.setStatus(BOOK_STATUS.CANCELLED);
                List<Seat> seats = book.getSeats();
                for (Seat seat : seats) {
                    seat.setStatus(SEAT_STATUS.FREE);
                }
                bookRepository.save(book);
                seatRepository.saveAll(seats);
            }
        } catch (Exception e) {
            log.error("CANCEL BOOK ERROR: {}", e.getMessage());
        }
    }

    @Transactional
    public ApiResponse deleteMovie(Long movieId) {
        try {
            Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
            if (!movie.isActive()) return ApiResponse.error(409, "Фильм уже удалён");
            List<Seance> seances = movie.getSeances();
            for (Seance seance : seances) {
                List<Book> books = bookRepository.findBySeanceIdAndStatusNot(seance.getId(), BOOK_STATUS.CANCELLED);
                for (Book book : books) {
                    book.setStatus(BOOK_STATUS.CANCELLED);
                    book.setSeats(null);
                }
                bookRepository.saveAll(books);
                List<Seat> seats = seatRepository.findBySeanceId(seance.getId());
                deleteAllSeats(seats);
            }
            deleteAllSeances(seances);
            movie.setActive(false);
            movieRepository.save(movie);
            return ApiResponse.success("Успешное удаление фильма");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    private void deleteAllSeances(List<Seance> seances) {
        seanceRepository.deleteAll(seances);
    }

    public void deleteAllSeats(List<Seat> seats) {
        seatRepository.deleteAll(seats);
    }

    public void freeAllSeats(List<Seat> seats) {
        try {
            for (Seat seat : seats) {
                seat.setStatus(SEAT_STATUS.FREE);
            }
            seatRepository.saveAll(seats);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public ApiResponse deleteAccount(String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
            List<Book> books = user.getBooks();
            for (Book book : books) {
                List<Seat> seats = book.getSeats();
                if (seats != null && !seats.isEmpty()) freeAllSeats(seats);
            }
            if (!books.isEmpty()) deleteAllBooks(books);
            userRepository.delete(user);
            return ApiResponse.success("Успешное удаление аккаунта");
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error();
        }
    }

    public ApiResponse deleteAccount(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            String email = user.getEmail();
            return deleteAccount(email);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error();
        }
    }

    public ApiResponse cancelSeance(Long seanceId) {
        try {
            Seance seance = seanceRepository.findById(seanceId).orElseThrow(SeanceNotFoundException::new);
            seance.setCancelled(true);
            seanceRepository.save(seance);
            List<Book> books = bookRepository.findBySeanceIdAndStatusNot(seanceId, BOOK_STATUS.CANCELLED);
            for (Book book : books) {
                book.setStatus(BOOK_STATUS.CANCELLED);
            }
            bookRepository.saveAll(books);
            Hall hall = seance.getHall();
            hall.getSeances().remove(seance);
            hallService.save(hall);
            return ApiResponse.success("Успешная отмена сеанса");
        } catch (Exception e) {
            return ApiResponse.error();
        }
    }
}
