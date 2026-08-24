package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.exception.SeanceNotFoundException;
import booking_system.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seat")
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/occupied")
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
}
