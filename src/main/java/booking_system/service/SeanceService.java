package booking_system.service;

import booking_system.entity.Seance;
import booking_system.exception.SeanceNotFoundException;
import booking_system.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
