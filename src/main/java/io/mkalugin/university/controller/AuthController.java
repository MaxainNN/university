package io.mkalugin.university.controller;

import io.mkalugin.university.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для авторизации.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Домашняя (стартовая) страница.
     */
    @GetMapping("/")
    @Operation(summary = "Домашняя страница",
            description = "Возвращает страницу логина или редиректит на dashboard если пользователь аутентифицирован")
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
    @Operation(summary = "Страница регистрации",
            description = "Возвращает HTML форму для регистрации нового пользователя")
    public String showRegistrationForm() {
        return "register";
    }

    /**
     * Регистрация пользователя.
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя и автоматически аутентифицирует его")
    public String register(
            @Parameter(description = "Имя пользователя", required = true) @RequestParam String username,
            @Parameter(description = "Email пользователя", required = true) @RequestParam String email,
            @Parameter(description = "Пароль пользователя", required = true) @RequestParam String password,
            HttpServletRequest request,
            Model model) {

        log.info("POST /register called for username={}, email={}", username, email);

        try {
            userService.registerUser(username, email, password);

            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                HttpSession session = request.getSession();
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                log.info("User {} authenticated successfully", username);
                return "redirect:/dashboard";

            } catch (AuthenticationException e) {
                log.error("Auto-login failed for user {}: {}", username, e.getMessage());
                model.addAttribute("success", "Registration successful! Please login.");
                return "login";
            }
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
    @Operation(summary = "Выход из системы",
            description = "Завершает сессию пользователя и перенаправляет на главную страницу")
    public String logout() {
        return "redirect:/";
    }
}
