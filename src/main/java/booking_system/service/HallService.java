package booking_system.service;

import booking_system.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;

    public long getHallCount() {
        return hallRepository.count();
    }
}
