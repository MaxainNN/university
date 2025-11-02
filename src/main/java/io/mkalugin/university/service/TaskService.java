package io.mkalugin.university.service;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с задачами.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Создание задачи.
     *
     * @param title
     * @param description
     * @param dueDate
     * @param priority
     * @param user
     */
    public Task createTask(String title, String description, Task.TaskPriority priority,
                           LocalDateTime dueDate, User user) {
        Task task = new Task(title, user);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    /**
     * Получение задачи по идентификатору пользователя.
     *
     * @param userId
     */
    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Получение списка задач по статусу.
     *
     * @param userId
     * @param status
     */
    public List<Task> getTasksByStatus(Long userId, Task.TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Получение списка задач по приоритету.
     *
     * @param userId
     * @param priority
     */
    public List<Task> getTasksByPriority(Long userId, Task.TaskPriority priority) {
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }

    /**
     * Получение списка срочных задач.
     *
     * @param userId
     */
    public List<Task> getOverdueTasks(Long userId) {
        return taskRepository.findOverdueTasks(userId);
    }

    /**
     * Обновление статуса задачи.
     *
     * @param taskId
     * @param status
     */
    public Task updateTaskStatus(Long taskId, Task.TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        if (status == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return taskRepository.save(task);
    }

    /**
     * Обновление задачи.
     *
     * @param taskId
     * @param title
     * @param description
     * @param priority
     * @param dueDate
     */
    public Task updateTask(Long taskId, String title, String description,
                           Task.TaskPriority priority, LocalDateTime dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        return taskRepository.save(task);
    }

    /**
     * Удаление задачи.
     *
     * @param taskId
     */
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}
