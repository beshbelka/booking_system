package booking_system.controller;

import booking_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminPageController {

    private final SeanceService seanceService;
    private final MovieService movieService;
    private final HallService hallService;
    private final BookService bookService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String admin() {
        return "admin";
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
