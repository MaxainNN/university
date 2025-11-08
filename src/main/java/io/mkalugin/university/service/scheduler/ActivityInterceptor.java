package io.mkalugin.university.service.scheduler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

/**
 * Перехватчик HTTP-запросов для отслеживания активности пользователей.
 * <p>
 * Каждый раз, когда пользователь делает запрос в систему,
 * данный интерсептор обновляет время его последней активности
 * через {@link UserActivityService}.
 * </p>
 * <p>
 * Работает только для аутентифицированных пользователей —
 * анонимные пользователи (например, неавторизованные запросы)
 * игнорируются.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ActivityInterceptor implements HandlerInterceptor {

    private final UserActivityService userActivityService;

    /**
     * Обрабатывает входящий HTTP-запрос перед его выполнением.
     * <p>
     * Если пользователь аутентифицирован, обновляет время его последней активности.
     * </p>
     *
     * @param request  текущий HTTP-запрос
     * @param response HTTP-ответ
     * @param handler  обработчик (контроллер или метод)
     * @return {@code true}, чтобы продолжить выполнение запроса
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            userActivityService.updateLastActive(username, Instant.now());
        }
        return true;
    }
}
