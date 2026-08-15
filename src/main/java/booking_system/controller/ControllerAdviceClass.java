package booking_system.controller;

import booking_system.exception.BaseException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ControllerAdviceClass {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    @ExceptionHandler(BaseException.class)
    public String handleBaseException(BaseException e, Model model) {
        model.addAttribute("errorCode", e.getErrorCode());
        model.addAttribute("errorMessage", e.getMessage());
        return "redirect:/error?code=" + e.getErrorCode() +
                "&message=" + e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        return "redirect:/error?code=500&message=Внутренняя ошибка сервера&description=Попробуйте позже";
    }
}
