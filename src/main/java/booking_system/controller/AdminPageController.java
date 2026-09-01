package booking_system.controller;

import booking_system.DTO.MovieFinanceRequest;
import booking_system.entity.Movie;
import booking_system.entity.User;
import booking_system.enums.SEAT_STATUS;
import booking_system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminPageController {

    private final SeanceService seanceService;
    private final MovieService movieService;
    private final HallService hallService;
    private final BookService bookService;
    private final UserService userService;
    private final SeatService seatService;

    @GetMapping("")
    public String admin() {
        return "admin";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("movieCount", movieService.getMovieCount());
        model.addAttribute("seanceCount", seanceService.getSeanceCount());
        model.addAttribute("hallCount", hallService.getHallCount());
        model.addAttribute("bookCount", bookService.getBookCount());
        model.addAttribute("movies", movieService.findAll());
        return "about";
    }

    @GetMapping("/control")
    public String control() {
        return "control";
    }

    @GetMapping("/control-films")
    public String films(Model model) {
        List<Movie> movies = movieService.findAll();
        if (!movies.isEmpty()) {
            movies.sort(Comparator.comparing(Movie::getId));
        }
        model.addAttribute("movies", movies);
        return "control-films";
    }

    @GetMapping("/control-films-edit")
    public String filmsEdit(@RequestParam Long movieId, Model model) {
        Movie movie = movieService.findById(movieId);
        model.addAttribute("movie", movie);
        return "control-films-edit";
    }

    @GetMapping("/control-users")
    public String users(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "control-users";
    }

    @GetMapping("/control-films-add")
    public String addFilm() { return "control-films-add"; }

    @GetMapping("/control-seances")
    public String seancesPage() { return "control-seances"; }

    @GetMapping("/control-seances-add")
    public String addSeancePage() { return "control-seances-add"; }

    @GetMapping("/control-bookings")
    public String bookingsPage() { return "control-bookings"; }

    @GetMapping("/finance")
    public String financePage(Model model) {
        String total = String.valueOf(bookService.total());
        Long soldTickets = seatService.countByStatus(SEAT_STATUS.BOOK);
        String averagePrice = String.valueOf(bookService.averagePrice(soldTickets));
        List<MovieFinanceRequest> movieFinanceRequests = movieService.finance();
        model.addAttribute("total", total);
        model.addAttribute("soldTickets", String.valueOf(soldTickets));
        model.addAttribute("averagePrice", averagePrice);
        model.addAttribute("movies", movieFinanceRequests);
        return "finance";
    }
}
