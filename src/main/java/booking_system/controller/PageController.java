package booking_system.controller;

import booking_system.entity.Book;
import booking_system.service.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PageController {

    private final SeanceService seanceService;
    private final MovieService movieService;
    private final HallService hallService;
    private final BookService bookService;
    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
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
                String email = jwtService.extractEmail(token);
                HashMap<String, String > nameAndBirthDate = userService.getNameAndBirthDate(email);
                List<Book> books = userService.getBooks(email);

                model.addAttribute("email", email);
                model.addAttribute("name", nameAndBirthDate.get("name"));
                model.addAttribute("birthDate", nameAndBirthDate.get("birthDate"));
                model.addAttribute("role", claims.get("role").equals("USER") ? "Пользователь" : "Администратор");
                model.addAttribute("books", books);

                return "profile";
            }
            return "redirect:/auth/login";
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam Long bookId, Model model) {
        Book book = bookService.findById(bookId);
        model.addAttribute("bookingId", bookId);
        model.addAttribute("book", book);
        return "payment";
    }

}
