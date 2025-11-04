package io.mkalugin.university.service;

import io.mkalugin.university.entity.User;
import io.mkalugin.university.exception.AuthenticationFailedException;
import io.mkalugin.university.exception.EmailExistException;
import io.mkalugin.university.exception.UserExistException;
import io.mkalugin.university.exception.UserNotFoundException;
import io.mkalugin.university.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя.
     *
     * @param username имя пользователя
     * @param email почта
     * @param password пароль
     * @return созданная сущность пользователя
     */
    public User registerUser(String username, String email, String password) {
        log.info("registerUser called for username={}, email={}", username, email);

        if (userRepository.existsByUsername(username)) {
            throw new UserExistException();
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailExistException();
        }

        User user = new User(username, email, passwordEncoder.encode(password));
        User saved = userRepository.save(user);
        log.info("User saved id={}, username={}", saved.getId(), saved.getUsername());

        return saved;
    }

    /**
     * Нахождение пользователя по имени.
     *
     * @param username имя пользователя
     * @return пользователь с указанным именем
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Нахождение пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным идентификатором
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Обновление пользователя.
     *
     * @param userId идентификатор пользователя
     * @param username имя пользователя
     * @param email почта
     * @param password пароль
     * @return обновленная сущность пользователя
     */
    public User updateUser(Long userId, String username, String email, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new UserExistException();
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new EmailExistException();
            }
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    /**
     * Аутентифицирует пользователя и сохраняет контекст безопасности в текущей HTTP-сессии.
     *
     * @param username       имя пользователя
     * @param password       пароль пользователя
     * @param servletRequest текущий HTTP-запрос, используемый для получения сессии
     * @throws AuthenticationFailedException если аутентификация не удалась
     */
    public void authenticateUser(String username, String password, HttpServletRequest servletRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = servletRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            log.info("User {} authenticated successfully via API", username);

        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException(username);
        }
    }
}
