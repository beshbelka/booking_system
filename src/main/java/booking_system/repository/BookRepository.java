package booking_system.repository;

import booking_system.entity.Book;
import booking_system.entity.Movie;
import booking_system.enums.BOOK_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByStatusAndCreatedAtBefore(BOOK_STATUS bookStatus, LocalTime expiryTime);

    List<Book> findBySeanceIdAndStatusNot(Long id, BOOK_STATUS bookStatus);

    List<Book> findBySeanceMovieId(Long movieId);
}
