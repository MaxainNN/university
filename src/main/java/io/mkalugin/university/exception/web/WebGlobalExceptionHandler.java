package io.mkalugin.university.exception.web;

import io.mkalugin.university.exception.BaseApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обрабочик ошибок в Веб контроллерах.
 */
@ControllerAdvice(basePackages = "io.mkalugin.university.controller.web")
@Slf4j
public class WebGlobalExceptionHandler {

    /**
     * Обработка исключения {@link Exception}
     * Базовое исключение
     *
     * @return html шаблон с ошибкой
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    /**
     * Обработка исключения {@link BaseApiException}
     * Исключение при ошибках в бизнес логике
     *
     * @return html шаблон с ошибкой
     */
    @ExceptionHandler(BaseApiException.class)
    public String handleBaseApiException(BaseApiException ex, Model model) {
        log.error("Business error: {}", ex.getMessage(), ex);
        model.addAttribute("status", ex.getHttpStatus().value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
