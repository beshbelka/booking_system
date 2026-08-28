package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.entity.Movie;
import booking_system.entity.Seance;
import booking_system.exception.SeanceNotFoundException;
import booking_system.service.SeanceService;
import booking_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final SeanceService seanceService;

    @GetMapping("/seat/occupied")
    public ResponseEntity<ApiResponse> seatsOccupied(@RequestParam Long seanceId) {
        try {
            if (seanceId == null || seanceId == 0) {
                throw new SeanceNotFoundException();
            }
            ApiResponse apiResponse = seatService.getSeatsOccupied(seanceId);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok().body(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    @GetMapping("/seats")
    public String chooseSeat(@RequestParam Long seanceId,
                             Model model) {
        Seance seance = seanceService.findById(seanceId);
        if (!seance.isCancelled() && seance.isAvailable()) {
            Movie movie = seance.getMovie();

            model.addAttribute("seance", seance);
            model.addAttribute("movie", movie);

            return "seats";
        }
        return "seance-unavailable";
    }
}
