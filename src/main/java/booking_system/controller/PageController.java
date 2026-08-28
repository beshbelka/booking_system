package booking_system.controller;

import booking_system.entity.Book;
import booking_system.enums.USER_ROLE;
import booking_system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PageController {

    private final MovieService movieService;
    private final BookService bookService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("movies", movieService.getAll());
        model.addAttribute("featuredMovie", movieService.getRandomMovie());
        return "mainPage";
    }

    @GetMapping("/auth/login")
    public String login() { return "login"; }

    @GetMapping("/auth/registration")
    public String registration() { return "registration"; }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/auth/login";
        }
        try {
            String email = authentication.getName();
            USER_ROLE role = userService.getRole(email);
            HashMap<String, String > nameAndBirthDate = userService.getNameAndBirthDate(email);
            List<Book> books = userService.getBooks(email);

            model.addAttribute("email", email);
            model.addAttribute("name", nameAndBirthDate.get("name"));
            model.addAttribute("birthDate", nameAndBirthDate.get("birthDate"));
            model.addAttribute("role", role.equals(USER_ROLE.USER) ? "Пользователь" : "Администратор");
            model.addAttribute("books", books);

            return "profile";
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
