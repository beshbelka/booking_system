package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Seance;
import booking_system.entity.Seat;
import booking_system.enums.BOOK_STATUS;
import booking_system.enums.SEAT_STATUS;
import booking_system.exception.BaseException;
import booking_system.exception.SeatNotFoundException;
import booking_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeanceService seanceService;

    public void save(Seat seat) {
        seatRepository.save(seat);
    }

    public void saveAll(List<Seat> seats) {
        seatRepository.saveAll(seats);
    }

    public Seat findByHallRowNumberAndSeance(Long hallId, short row, short number, Long seanceId) {
        return seatRepository.findByHallIdAndRowAndNumberAndSeanceId(hallId, row, number, seanceId)
                .orElseThrow(() -> new SeatNotFoundException(
                                row, number, hallId, seanceId)
                );
    }

    public ApiResponse getSeatsOccupied(Long seanceId) {
        try {
            Seance seance = seanceService.findById(seanceId);
            List<Seat> occupiedSeats = seatRepository.findBySeanceIdAndStatusNot(
                    seanceId,
                    SEAT_STATUS.FREE
            );
            HashMap<String, String> occupiedMap = new HashMap<>();
            int index = 1;
            for (Seat seat : occupiedSeats) {
                String key = String.valueOf(index++);
                String value = seat.getRow() + "-" + seat.getNumber();
                occupiedMap.put(key, value);
            }
            return ApiResponse.success(occupiedMap);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public List<Seat> findBySeanceIdAndStatusNot(Long id, SEAT_STATUS seatStatus) {
        try {
            return seatRepository.findBySeanceIdAndStatusNot(id, seatStatus);
        } catch (Exception e) {
            log.error("FIND BY SEANCE ID AND STATUS NOT ERROR: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Seat> findBySeanceId(Long id) {
        try {
            return seatRepository.findBySeanceId(id);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
