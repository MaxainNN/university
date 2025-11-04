package io.mkalugin.university.dto;

import io.mkalugin.university.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO для представления информации об ошибке.
 *
 * <p>Используется в качестве ответа при возникновении ошибок.
 * Содержит основное сообщение, код ошибки, HTTP-статус,
 * метку времени, путь запроса и дополнительные детали.</p>
 */
@Getter
@Builder
@Schema(description = "Ответ об ошибке")
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибке", example = "Неверный запрос:  пользователь не найден")
    private final String message;

    @Schema(description = "Код ошибки", example = "USER_NOT_FOUND")
    private final ErrorCode error;

    @Schema(description = "HTTP статус", example = "404")
    private final int status;

    @Schema(description = "Дата и время запроса", example = "2025-11-04T00:00:00.000Z")
    @Builder.Default
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
}
