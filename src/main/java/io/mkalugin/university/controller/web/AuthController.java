package io.mkalugin.university.controller.web;

import io.mkalugin.university.exception.AuthenticationFailedException;
import io.mkalugin.university.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web Контроллер для авторизации.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Домашняя (стартовая) страница.
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    /**
     * Страница регистрации.
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * Регистрация пользователя.
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        try {
            userService.registerUser(username, email, password);
            userService.authenticateUser(username, password, request);
            return "redirect:/dashboard";

        } catch (AuthenticationFailedException e) {
            log.error("Auto-login failed for user {}: {}", username, e.getMessage());
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";

        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    /**
     * Выход из системы.
     */
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}
