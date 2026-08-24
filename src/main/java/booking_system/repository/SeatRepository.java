package booking_system.repository;

import booking_system.entity.Seance;
import booking_system.entity.Seat;
import booking_system.enums.SEAT_STATUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByHallIdAndRowAndNumberAndSeanceId(
            Long hallId,
            short row,
            short number,
            Long seanceId
    );

    List<Seat> findBySeanceIdAndStatusNot(Long seanceId, SEAT_STATUS seatStatus);
}
