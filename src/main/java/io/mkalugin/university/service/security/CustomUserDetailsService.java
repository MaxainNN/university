package io.mkalugin.university.service.security;

import io.mkalugin.university.entity.User;
import io.mkalugin.university.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для загрузки данных пользователя в Spring Security.
 *
 * <p>Данный сервис отвечает за аутентификацию пользователей путем поиска в репозитории
 * и преобразования сущности пользователя в объект {@link UserDetails}, используемый Spring Security.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает данные пользователя по имени пользователя для аутентификации.
     *
     * <p>Метод выполняет поиск пользователя в репозитории по указанному имени пользователя.
     * Если пользователь найден, создается объект {@link UserDetails} с данными пользователя
     * и ролью "USER". Если пользователь не найден, выбрасывается исключение.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        log.info("User found: {}, password: {}", user.getUsername(), user.getPassword());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
