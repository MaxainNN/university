package io.mkalugin.university.dto;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель для получения данных по задаче.
 */
@Data
@Schema(description = "Ответ с информацией о задаче")
public class TaskResponse {

    @Schema(description = "ID задачи", example = "1")
    private Long id;

    @Schema(description = "Название задачи", example = "Завершить проект")
    private String title;

    @Schema(description = "Описание задачи", example = "Необходимо завершить все задачи проекта до конца недели")
    private String description;

    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Срок выполнения задачи", example = "2024-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Дата завершения задачи", example = "2024-12-25T10:00:00")
    private LocalDateTime completedAt;

    @Schema(description = "Имя пользователя")
    private String userName;

    /**
     * Конструктор с полем username
     */
    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.dueDate = task.getDueDate();
        this.completedAt = task.getCompletedAt();
        this.userName = task.getUser().getUsername();
    }
}
