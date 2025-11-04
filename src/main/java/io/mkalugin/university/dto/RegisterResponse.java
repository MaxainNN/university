package io.mkalugin.university.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO ответа после регистрации пользователя.
 */
@Data
@AllArgsConstructor
public class RegisterResponse {

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Пароль
     */
    private String password;
}
