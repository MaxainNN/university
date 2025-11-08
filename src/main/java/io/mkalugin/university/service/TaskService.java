package io.mkalugin.university.service;

import io.mkalugin.university.dto.TaskCreateRequest;
import io.mkalugin.university.dto.TaskResponse;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.exception.UserNotFoundException;
import io.mkalugin.university.mapper.TaskMapper;
import io.mkalugin.university.repository.TaskRepository;
import io.mkalugin.university.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "tasks")
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    /**
     * Создание задачи.
     *
     * @param request запрос на создание задачи
     * @return созданная сущность задачи
     */
    @CacheEvict(allEntries = true)
    public Task createTask(TaskCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Task task = taskMapper.toEntity(request, user);
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
    //@Cacheable(key = "{#user.id, #page, #size}")
    public Page<TaskResponse> getTasksByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").descending());
        return taskRepository.findAllByUser(user, pageable)
                .map(TaskResponse::new);
    }

    /**
     * Получение списка задач по статусу.
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @return список задач
     */
    @Cacheable(key = "{#userId, #status}")
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
    @Cacheable(key = "{#userId, #priority}")
    public List<Task> getTasksByPriority(Long userId, TaskPriority priority) {
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }

    /**
     * Получение списка актуальных задач.
     *
     * @param userId идентификатор пользователя
     * @return список задач
     */
    @Cacheable(key = "#userId + '_overdue'")
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
    @CacheEvict(allEntries = true)
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return taskRepository.save(task);
    }
}
