package io.mkalugin.university.dto;

import io.mkalugin.university.enums.TaskPriority;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для запроса для создания задачи.
 */
@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long userId;
}
