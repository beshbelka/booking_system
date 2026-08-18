package booking_system.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;

import booking_system.exception.BaseException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
        // Получаем исключение из атрибутов запроса
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        int code = 500;
        String message = "Внутренняя ошибка сервера";

        if (exception instanceof Throwable ex) {

            if (ex instanceof BaseException baseEx) {
                code = baseEx.getErrorCode();
                message = baseEx.getMessage();
            } else {
                message = ex.getMessage() != null ? ex.getMessage() : "Внутренняя ошибка сервера";
            }
        }

        if (code == 401) {
            System.out.println("TRUE");
            response.sendRedirect("/auth/login");
        }

        model.addAttribute("errorCode", code);
        model.addAttribute("errorMessage", message);

        return "error";
    }
}