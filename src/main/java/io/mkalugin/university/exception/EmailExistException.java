package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Исключение, выбрасываемое, если пользователь с таким email уже существует.
 */
public class EmailExistException extends BaseApiException {

    public EmailExistException() {
        super(ErrorCode.EMAIL_EXISTS, HttpStatus.CONFLICT);
    }
}
