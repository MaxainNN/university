package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Исключение , выбрасываемое при ошибки аутентификации.
 */
@Slf4j
public class AuthenticationFailedException extends BaseApiException {

    public AuthenticationFailedException(String username) {
        super(ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        log.error("Auto-login failed for user {}", username);
    }
}
