package io.mkalugin.university.dto;

import lombok.Data;

/**
 * DTO запроса на регистрацию пользователя.
 */
@Data
public class RegisterRequest {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Почта
     */
    private String email;

    /**
     * Пароль
     */
    private String password;
}
