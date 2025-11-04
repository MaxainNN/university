package io.mkalugin.university.controller.api;

import io.mkalugin.university.dto.RegisterResponse;
import io.mkalugin.university.dto.RegisterRequest;
import io.mkalugin.university.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер с методами для аутентификации.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Authentication API", description = "REST API для аутентификации")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final UserService userService;

    /**
     * Регистрация и аутентификация пользователя с помошью API.
     *
     * @param request запрос с данными
     * @param servletRequest текущий HTTP-запрос, используемый для получения сессии
     * @return RegisterResponse с переданными именем и паролем
     */
    @PostMapping("/register")
    @Operation(summary = " Регистрация и аутентификация пользователя")
    public RegisterResponse register(
            @Parameter(description = "Данные для регистрации") @RequestBody RegisterRequest request,
            HttpServletRequest servletRequest) {
        log.info("API POST /api/register called for username={}, email={}", request.getUsername(), request.getEmail());

        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        userService.authenticateUser(request.getUsername(), request.getPassword(), servletRequest);
        return new RegisterResponse(request.getUsername(), request.getPassword());
    }
}
