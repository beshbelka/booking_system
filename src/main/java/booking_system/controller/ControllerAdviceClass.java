package booking_system.controller;

import booking_system.enums.USER_ROLE;
import booking_system.exception.BaseException;
import booking_system.service.AuthService;
import booking_system.service.JwtService;
import booking_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ControllerAdviceClass {

    private final AuthService authService;

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(HttpServletResponse response, HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                return true;
            }
            return authService.isAuthenticated(request, response);
        } catch (Exception e) {
            return false;
        }
    }

    @ExceptionHandler(BaseException.class)
    public String handleBaseException(BaseException e, Model model) {
        model.addAttribute("errorCode", e.getErrorCode());
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "Внутренняя ошибка сервера");
        return "error";
    }
}
