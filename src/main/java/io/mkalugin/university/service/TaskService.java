package io.mkalugin.university.service;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
     * @param title заголовок
     * @param description описание
     * @param dueDate срок выполнения
     * @param priority приоритет
     * @param user пользователь
     * @return созданная сущность задачи
     */
    public Task createTask(String title, String description, TaskPriority priority,
                           LocalDateTime dueDate, User user) {
        Task task = new Task(title, user);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    /**
     * Получение списка задач пользователя
     * с пагинацией.
     *
     * @param user пользователь
     * @param page страница
     * @param size количество элементов
     * @return спискок задач пользователя
     */
    public Page<Task> getTasksByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").descending());
        return taskRepository.findAllByUser(user, pageable);
    }

    /**
     * Получение списка задач по статусу.
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @return список задач
     */
    public List<Task> getTasksByStatus(Long userId, TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Получение списка задач по приоритету.
     *
     * @param userId идентификатор пользователя
     * @param priority приоритет
     * @return список задач
     */
    public List<Task> getTasksByPriority(Long userId, TaskPriority priority) {
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }

    /**
     * Получение списка актуальных задач.
     *
     * @param userId идентификатор пользователя
     * @return список задач
     */
    public List<Task> getOverdueTasks(Long userId) {
        return taskRepository.findOverdueTasks(userId);
    }

    /**
     * Обновление статуса задачи.
     *
     * @param taskId идентификатор задачи
     * @param status статус
     * @return обновленная сущность задачи
     */
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return taskRepository.save(task);
    }

    /**
     * Обновление задачи.
     *
     * @param taskId идентификатор задачи
     * @param title заголовок
     * @param description описание
     * @param priority приритет
     * @param dueDate дата выполнения
     * @return обновленная сущность задачи
     */
    public Task updateTask(Long taskId, String title, String description,
                           TaskPriority priority, LocalDateTime dueDate) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        return taskRepository.save(task);
    }
}
