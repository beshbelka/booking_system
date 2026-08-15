package booking_system.service;

import booking_system.entity.Seat;
import booking_system.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public void save(Seat seat) {
        seatRepository.save(seat);
    }
}
