package booking_system.controller;

import booking_system.service.BookService;
import booking_system.service.HallService;
import booking_system.service.MovieService;
import booking_system.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @Autowired
    private SeanceService seanceService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private HallService hallService;
    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public String home(Model model) {
        // все фильмы
        model.addAttribute("movies", movieService.getAllMoviesWithSeances());

        // сеанс недели
        model.addAttribute("featuredMovie", movieService.getRandomMovie());

        return "mainPage";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("movieCount", movieService.getMovieCount());
        model.addAttribute("seanceCount", seanceService.getSeanceCount());
        model.addAttribute("hallCount", hallService.getHallCount());
        model.addAttribute("bookCount", bookService.getBookCount());
        model.addAttribute("movies", movieService.getAllMovies());
        return "about";
    }
}
