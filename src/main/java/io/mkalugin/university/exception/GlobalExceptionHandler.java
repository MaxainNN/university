package io.mkalugin.university.exception;

import io.mkalugin.university.dto.ErrorResponse;
import io.mkalugin.university.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения {@link Exception}
     * Базовое исключение
     *
     * @return ErrorResponse с данными об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGenericException(Exception ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error(ErrorCode.INTERNAL_SERVER_ERROR)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    /**
     * Обработка исключения {@link BaseApiException}
     * Исключение при ошибках в бизнес логике
     *
     * @return ErrorResponse с данными об ошибке
     */
    @ExceptionHandler(BaseApiException.class)
    public ErrorResponse handleBaseApiException(BaseApiException ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .error(ex.getErrorCode())
                .status(ex.getHttpStatus().value())
                .build();
    }
}
