package io.mkalugin.university.service;

import io.mkalugin.university.dto.TaskCreateRequest;
import io.mkalugin.university.dto.TaskResponse;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.exception.TaskNotFoundException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Task createTask(TaskCreateRequest request) {
        log.debug("Creating task {}", request.getTitle());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
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
    public Page<TaskResponse> getTasksByUser(User user, int page, int size) {
        log.info("Getting tasks for userId = {} with pagination", user.getId());

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
        log.info("Getting tasks by status for userId =  {}", userId);
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
        log.info("Getting tasks by priority for userId = {}", userId);
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }

    /**
     * Получение списка просроченных задач.
     *
     * @param userId идентификатор пользователя
     * @return список задач
     */
    @Cacheable(key = "#userId + '_overdue'")
    public List<Task> getOverdueTasks(Long userId) {
        log.info("Getting list of overdue tasks for userId = {}", userId);
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
    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status) {
        log.info("Updating status of task with id {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        return taskRepository.save(task);
    }

    /**
     * Получение списка задач по приоритету и статусу.
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @param priority приоритет
     * @return список задач
     */
    @Cacheable(key = "{#userId, #status, #priority}")
    public List<TaskResponse> searchTasks(Long userId, TaskStatus status, TaskPriority priority) {
        log.info("Getting task for userId = {} and status {} and priority = {}", userId, status, priority);

        List<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority);
        } else if (status != null) {
            tasks = taskRepository.findByUserIdAndStatus(userId, status);
        } else if (priority != null) {
            tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
            return getTasksByUser(user, 0, Integer.MAX_VALUE).getContent();
        }

        return tasks.stream()
                .map(TaskResponse::new)
                .toList();
    }
}
