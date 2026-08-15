package booking_system.controller;

import booking_system.entity.Movie;
import booking_system.entity.Seance;
import booking_system.service.MovieService;
import booking_system.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SeanceController {

    private final SeanceService seanceService;
    private final MovieService movieService;

    @GetMapping("/seats")
    public String chooseSeat(@RequestParam Long seanceId,
                             @RequestParam Long movieId,
                             Model model) {
        Movie movie = movieService.findById(movieId);
        Seance seance = seanceService.findById(seanceId);

        model.addAttribute("seance", seance);
        model.addAttribute("movie", movie);

        return "seats";
    }
}
