package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое, если пользователь с таким именем уже существует.
 */
public class UserExistException extends BaseApiException {

    public UserExistException() {
        super(ErrorCode.USERNAME_EXISTS, HttpStatus.CONFLICT);
    }
}
