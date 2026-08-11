package booking_system.controller;

import booking_system.service.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

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
        model.addAttribute("movies", movieService.getAllMovies());

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
    public String profile(HttpServletRequest request, Model model) {
        String token = jwtService.extractTokenFromCookies(request);
        if (token == null) return "redirect:/auth/login";
        try {
            if (jwtService.isTokenValid(token)) {

                Claims claims = jwtService.extractClaims(token);

                model.addAttribute("email", jwtService.extractEmail(token));
                model.addAttribute("name", claims.get("name"));
                model.addAttribute("birthDate", claims.get("birthDate"));
                model.addAttribute("role", claims.get("role").equals("USER") ? "Пользователь" : "Администратор");

                return "profile";
            }
            return "redirect:/auth/login";
        } catch (Exception e) {
            log.error("PageController: token validate error,  " + e.getMessage());
            return "redirect:/auth/login";
        }
    }

}
