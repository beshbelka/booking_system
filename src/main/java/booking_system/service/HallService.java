package booking_system.service;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Hall;
import booking_system.entity.Seance;
import booking_system.exception.BaseException;
import booking_system.repository.HallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
}
