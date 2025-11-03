package io.mkalugin.university.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация для шифрования паролей пользователей.
 * <p>
 * Определяет используемый алгоритм хэширования паролей
 * в приложении (BCrypt).
 * </p>
 */
@Configuration
public class PasswordConfig {

    /**
     * Создает и предоставляет {@link PasswordEncoder} на основе BCrypt.
     *
     * @return объект {@link PasswordEncoder}, используемый для хэширования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
