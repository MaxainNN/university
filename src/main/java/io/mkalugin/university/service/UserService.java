package io.mkalugin.university.service;

import io.mkalugin.university.entity.User;
import io.mkalugin.university.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }
}
