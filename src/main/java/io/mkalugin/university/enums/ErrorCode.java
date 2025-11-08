package io.mkalugin.university.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import io.mkalugin.university.dto.ErrorResponse;

/**
 * Enum для кодов ошибки.
 *
 * <p>Используется в {@link ErrorResponse} для идентификации типа ошибки.</p>
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * Внутренняя ошибка сервера
     */
    INTERNAL_SERVER_ERROR("Internal server error"),

    /**
     * Пользователь не найден
     */
    USER_NOT_FOUND("User not found"),

    /**
     * Пользователь с таким именем уже существует
     */
    USERNAME_EXISTS("Username already exists"),

    /**
     * Пользователь с таким email уже существует
     */
    EMAIL_EXISTS("Email already exists"),

    /**
     * Ошибка при аутентификации
     */
    AUTHENTICATION_FAILED("Auto-login failed"),

    /**
     * Задача не найдена
     */
    TASK_NOT_FOUND("Task not found");

    /**
     * Строковое представление ошибки
     */
    private final String value;
}
