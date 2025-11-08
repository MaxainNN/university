package io.mkalugin.university.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для запроса для создания события.
 */
@Data
@Schema(description = "Запрос для создания события")
public class EventCreateRequest {

    @Schema(description = "Название события", example = "Созвониться по проекту")
    private String title;

    @Schema(description = "Дата начала", example = "2025-12-31T23:59:59")
    private LocalDateTime startTime;

    @Schema(description = "Дата окончания", example = "2025-12-31T23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "Аннотация", example = "Важный созвон")
    private String annotation;

    @Schema(description = "Заметки", example = "Подготовить отчет")
    private String notes;

    @Schema(description = "Время напоминания", example = "2025-12-31T23:59:59")
    private LocalDateTime reminderTime;

    @Schema(description = "Имя пользователя")
    private Long userId;
}
