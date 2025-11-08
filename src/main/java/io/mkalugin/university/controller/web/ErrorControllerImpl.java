package io.mkalugin.university.controller.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для обработки ошибок приложения.
 * Обрабатывает все ошибки и отображает страницу с информацией об ошибке.
 */
@Controller
public class ErrorControllerImpl implements ErrorController {

    /**
     * Обрабатывает ошибки и отображает страницу ошибки.
     *
     * @param request HTTP запрос
     * @param model модель для передачи данных в представление
     * @return шаблон страницы ошибки
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        model.addAttribute("status", statusCode);
        model.addAttribute("message", message);

        return "error";
    }
}
