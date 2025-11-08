package io.mkalugin.university.dto;

import io.mkalugin.university.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для запроса для создания задачи.
 */
@Data
@Schema(description = "Запрос для создания задачи")
public class TaskCreateRequest {

    @Schema(description = "Название задачи", example = "Завершить проект")
    private String title;

    @Schema(description = "Описание задачи", example = "Необходимо завершить все задачи проекта до конца недели")
    private String description;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Срок выполнения задачи", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Имя пользователя")
    private Long userId;
}
