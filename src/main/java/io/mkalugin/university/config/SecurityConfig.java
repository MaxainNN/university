package io.mkalugin.university.config;

import io.mkalugin.university.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности.
 * <p>
 * Класс отвечает за настройку аутентификации, авторизации,
 * обработку входа и выхода пользователей, а также разрешение доступа
 * к статическим ресурсам (CSS, JS, изображения и т.д.).
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Определяет конфигурацию цепочки фильтров безопасности (SecurityFilterChain).
     * Настраивает правила доступа, параметры формы входа, выхода и обработку CSRF.
     *
     * @param http объект конфигурации безопасности HTTP-запросов
     * @return настроенный объект {@link SecurityFilterChain}
     * @throws Exception если происходит ошибка при конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login",
                                "/styles/**", "/images/**", "/js/**", "/fonts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Предоставляет {@link AuthenticationManager}, используемый для аутентификации пользователей.
     *
     * @param configuration конфигурация аутентификации Spring
     * @return объект {@link AuthenticationManager}
     * @throws Exception если не удалось создать менеджер аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Создает и настраивает {@link DaoAuthenticationProvider}, который используется
     * для проверки учетных данных пользователей с использованием {@link CustomUserDetailsService}
     * и {@link PasswordEncoder}.
     *
     * @return настроенный объект {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
