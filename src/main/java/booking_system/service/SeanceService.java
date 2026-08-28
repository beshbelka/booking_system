package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Book;
import booking_system.entity.Seance;
import booking_system.enums.BOOK_STATUS;
import booking_system.exception.SeanceNotFoundException;
import booking_system.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceService {

    private final SeanceRepository seanceRepository;

    public long getSeanceCount() {
        return seanceRepository.count();
    }

    public List<Seance> findByMovieId(Long movieId) {
        return seanceRepository.findByMovieId(movieId);
    }

    public Seance findById(Long seanceId) {
        return seanceRepository.findById(seanceId).orElseThrow(SeanceNotFoundException::new);
    }

    public List<Seance> findByMovieIdAndHallId(Long movieId, Long hallId) {
        try {
            return seanceRepository.findByMovieIdAndHallId(movieId, hallId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Seance> findByHallId(Long hallId) {
        try {
            return seanceRepository.findByHallId(hallId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Seance> findAll() {
        return seanceRepository.findAll();
    }

    public void save(Seance seance) {
        seanceRepository.save(seance);
    }

    public List<Seance> findOverlappingSeancesNotCancelled(Long hallId, LocalTime startTime, LocalTime endTime) {
        return seanceRepository.findOverlappingSeancesNotCancelled(hallId, startTime, endTime);
    }
}
