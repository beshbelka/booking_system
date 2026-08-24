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

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SeanceController {

    private final SeanceService seanceService;
    private final MovieService movieService;

    @GetMapping("/seances")
    public String showSeance(@RequestParam Long movieId, Model model) {
        Movie movie = movieService.findById(movieId);
        List<Seance> seances = seanceService.findByMovieId(movieId);

        model.addAttribute("movie", movie);
        if (!seances.isEmpty()) {
            seances.sort(Comparator.comparing(Seance::getStart_time));
        }
        model.addAttribute("seances", seances);

        return "seances";
    }
}
