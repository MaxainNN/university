package io.mkalugin.university.exception;

import io.mkalugin.university.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Базовый класс для бизнес-исключений.
 */
@Getter
public abstract class BaseApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    protected BaseApiException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getValue());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
