package booking_system.service;

import booking_system.repository.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HallService {

    @Autowired
    private HallRepository hallRepository;

    public long getHallCount() {
        return hallRepository.count();
    }
}
