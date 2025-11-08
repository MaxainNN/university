package io.mkalugin.university.dto;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель для получения данных по задаче.
 */
@Data
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
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
