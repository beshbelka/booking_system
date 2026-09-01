package booking_system.controller;

import booking_system.DTO.ApiResponse;
import booking_system.exception.BaseException;
import booking_system.exception.SeanceNotFoundException;
import booking_system.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;

    @GetMapping("/hall/info")
    public ResponseEntity<ApiResponse> getHallInfo(@RequestParam Long seanceId) {
        try {
            if (seanceId == null || seanceId == 0) {
                throw new SeanceNotFoundException();
            }
            ApiResponse apiResponse = hallService.getHallInfo(seanceId);
            if (apiResponse.isSuccess()) {
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity
                    .status(apiResponse.getCode())
                    .body(apiResponse);
        } catch (BaseException e) {
            return ResponseEntity
                    .status(e.getErrorCode())
                    .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error());
        }
    }
}
