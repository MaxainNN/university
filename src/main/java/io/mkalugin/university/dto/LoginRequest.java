package io.mkalugin.university.dto;

import lombok.Data;

/**
 * DTO запроса для аутентификации
 */
@Data
public class LoginRequest {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Пароль
     */
    private String password;
}
