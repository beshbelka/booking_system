package booking_system.service;

import booking_system.entity.Seance;
import booking_system.repository.SeanceRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;

    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }

    public long getSeanceCount() {
        return seanceRepository.count();
    }
}
