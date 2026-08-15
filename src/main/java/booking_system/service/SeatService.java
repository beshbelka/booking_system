package booking_system.service;

import booking_system.entity.Seat;
import booking_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public void save(Seat seat) {
        seatRepository.save(seat);
    }
}
