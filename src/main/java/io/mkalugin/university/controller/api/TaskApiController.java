package io.mkalugin.university.controller.api;

import io.mkalugin.university.dto.TaskCreateRequest;
import io.mkalugin.university.dto.TaskResponse;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.repository.UserRepository;
import io.mkalugin.university.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер с методами для работы с задачами.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Tasks API", description = "REST API для работы с задачами")
public class TaskApiController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    /**
     * Создание задачи
     *
     * @param request запрос с данными для создания задачи
     * @return ответ с данными созданной задачи
     */
    @PostMapping
    @Operation(
            summary = "Создание новой задачи",
            description = "Создает задачу для указанного пользователя"
    )
    public ResponseEntity<Task> createTask(
            @RequestBody @Valid TaskCreateRequest request) {
        Task task = taskService.createTask(request);
        return ResponseEntity.ok(task);
    }

    /**
     * Получение задач пользователя с пагинацией
     *
     * @param userId идентификатор пользователя
     * @param page номер страеицы
     * @param size количество элементов
     * @return список задач
     */
    @GetMapping("/tasks/{userId}")
    @Operation(
            summary = "Получение задач пользователя с пагинацией",
            description = "Возвращает страницу с задачами пользователя, " +
                    "отсортированными по дате выполнения (по убыванию)"
    )
    public ResponseEntity<Page<TaskResponse>> getTasksByUser(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        User user = userRepository.findById(userId).get();

        Page<TaskResponse> tasks = taskService.getTasksByUser(user, page, size);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Получение задач пользователя по статусу
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @return список задач
     */
    @GetMapping("/tasks/{userId}/status/{status}")
    @Operation(
            summary = "Получение задач пользователя по статусу",
            description = "Возвращает список задач пользователя с указанным статусом"
    )
    public ResponseEntity<List<Task>> getTasksByStatus(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Статус задачи", example = "IN_PROGRESS")
            @PathVariable TaskStatus status) {

        List<Task> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Получение задач пользователя по приоритету
     *
     * @param userId идентификатор пользователя
     * @param priority приоритет
     * @return список задач
     */
    @GetMapping("/tasks/{userId}/priority/{priority}")
    @Operation(
            summary = "Получение задач пользователя по приоритету",
            description = "Возвращает список задач пользователя с указанным приоритетом"
    )
    public ResponseEntity<List<Task>> getTasksByPriority(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Приоритет задачи", example = "HIGH")
            @PathVariable TaskPriority priority) {

        List<Task> tasks = taskService.getTasksByPriority(userId, priority);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Получение списка просроченных задач
     *
     * @param userId идентификатор пользователя
     * @return список задач
     */
    @GetMapping("/tasks/{userId}/overdue")
    @Operation(
            summary = "Получение просроченных задач пользователя",
            description = "Возвращает список просроченных задач пользователя"
    )
    public ResponseEntity<List<Task>> getOverdueTasks(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId) {

        List<Task> tasks = taskService.getOverdueTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Обновление статуса задачи
     *
     * @param taskId идентификатор задачи
     * @param status статус
     * @return ответ с обновленными данными задачи
     */
    @PatchMapping("/tasks/{taskId}/status")
    @Operation(
            summary = "Обновление статуса задачи",
            description = "Обновляет статус задачи. При установке статуса COMPLETED " +
                    "автоматически устанавливается время завершения"
    )
    public ResponseEntity<Task> updateTaskStatus(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long taskId,

            @Parameter(description = "Новый статус задачи", example = "COMPLETED")
            @RequestParam TaskStatus status) {

        Task task = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(task);
    }

    /**
     * Получение списка задач по приоритету и статусу.
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @param priority приоритет
     * @return список задач
     */
    @GetMapping("/tasks/{userId}/search")
    @Operation(
            summary = "Поиск задач по различным критериям",
            description = "Возвращает задачи пользователя с возможностью фильтрации по статусу и приоритету"
    )
    public ResponseEntity<List<TaskResponse>> searchTasks(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,

            @Parameter(description = "Статус задачи", example = "IN_PROGRESS")
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Приоритет задачи", example = "HIGH")
            @RequestParam(required = false) TaskPriority priority) {

        List<TaskResponse> tasks = taskService.searchTasks(userId, status, priority);
        return ResponseEntity.ok(tasks);
    }
}
