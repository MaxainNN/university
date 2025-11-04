package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое, если пользователь не найден.
 */
public class UserNotFoundException extends BaseApiException  {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}
