package booking_system.service;

import booking_system.entity.Seat;
import booking_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public void save(Seat seat) {
        seatRepository.save(seat);
    }

    public void saveAll(List<Seat> seats) {
        seatRepository.saveAll(seats);
    }
}
