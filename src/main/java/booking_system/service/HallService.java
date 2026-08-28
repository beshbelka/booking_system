package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Hall;
import booking_system.entity.Seance;
import booking_system.exception.BaseException;
import booking_system.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeanceService seanceService;

    public long getHallCount() {
        return hallRepository.count();
    }

    public ApiResponse getHallInfo(Long seanceId) {
        try {
            Seance seance = seanceService.findById(seanceId);
            Hall hall = seance.getHall();
            HashMap<String, String> hallInfo = new HashMap<String, String>();
            hallInfo.put("id", hall.id.toString());
            hallInfo.put("rows", String.valueOf(hall.getRows()));
            hallInfo.put("seatsPerRow", String.valueOf(hall.getSeatsPerRow()));
            return ApiResponse.success(hallInfo);
        } catch (BaseException e) {
            return ApiResponse.error(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    public Hall findById(Long hallId) {
        try {
            return hallRepository.findById(hallId).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            return null;
        }
    }

    public void save(Hall hall) {
        hallRepository.save(hall);
    }

    public boolean isHallAvailable(Long seanceId, Long hallId, LocalTime startTime, LocalTime endTime) {
        try {
            List<Seance> overlapping = seanceService.findOverlappingSeancesNotCancelled(hallId, startTime, endTime);
            if (seanceId != 0) overlapping.removeIf(seance -> seance.getId().equals(seanceId));
            return overlapping.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
