package booking_system.repository;

import booking_system.entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {
    List<Seance> findAll();
    List<Seance> findByMovieId(Long movieId);

    List<Seance> findByMovieIdAndHallId(Long movieId, Long hallId);

    List<Seance> findByHallId(Long hallId);

    @Query("SELECT s FROM Seance s WHERE s.hall.id = :hallId AND " +
            "s.start_time < :endTime AND s.end_time > :startTime")
    List<Seance> findOverlappingSeances(@Param("hallId") Long hallId,
                                        @Param("startTime") LocalTime startTime,
                                        @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM Seance s WHERE s.hall.id = :hallId AND " +
            "s.start_time < :endTime AND s.end_time > :startTime AND " +
            "s.cancelled = false")
    List<Seance> findOverlappingSeancesNotCancelled(@Param("hallId") Long hallId,
                                                    @Param("startTime") LocalTime startTime,
                                                    @Param("endTime") LocalTime endTime);
}
