package booking_system.controller;

import booking_system.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class PageController {

    @Autowired
    private SeanceService seanceService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private HallService hallService;
    @Autowired
    private BookService bookService;
    @Autowired
    private JwtService jwtService;

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

    @GetMapping("/auth/login")
    public String login() { return "login"; }

    @GetMapping("/auth/registration")
    public String registration() { return "registration"; }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) return "/auth/login";
        try {
            if (jwtService.isTokenValid(token)) {
                return "/profile";
            }
            return "/auth/login";
        } catch (Exception e) {
            log.error("AuthController: token validate error " + e.getMessage());
            return "/auth/login";
        }
    }
}
