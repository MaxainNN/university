package io.mkalugin.university.controller;

import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.service.EventService;
import io.mkalugin.university.service.TaskService;
import io.mkalugin.university.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер с дашбордом.
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API для работы с dashboard - событиями и задачами")
public class DashboardController {

    private final EventService eventService;
    private final TaskService taskService;
    private final UserService userService;

    /**
     * Главная страница дашборда.
     */
    @GetMapping
    @Operation(summary = "Главная страница dashboard",
            description = "Возвращает dashboard с событиями, задачами и формами для их создания")
    public String dashboard(
            Authentication authentication,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Task> taskPage = taskService.getTasksByUser(user, page, size);
        List<Event> events = eventService.getUserEvents(user.getId());
        List<Task> overdueTasks = taskService.getOverdueTasks(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        return "dashboard";
    }

    /**
     * Календарь пользователя.
     */
    @GetMapping("/calendar")
    @Operation(summary = "Страница календаря",
            description = "Возвращает страницу с календарем событий")
    public String calendar(@RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Event> events = eventService.getUserEventsByDateRange(user.getId(), start, end);

        Map<Integer, List<Event>> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartTime().getDayOfMonth()));

        model.addAttribute("user", user);
        model.addAttribute("events", eventsByDay);
        model.addAttribute("selectedDate", date);

        return "calendar";
    }

    /**
     * Добавление события.
     */
    @PostMapping("/events")
    @Operation(summary = "Создание нового события",
            description = "Создает новое событие в календаре пользователя")
    public String createEvent(
            @Parameter(description = "Название события", required = true) @RequestParam String title,
            @Parameter(description = "Время начала события", required = true) @RequestParam LocalDateTime startTime,
            @Parameter(description = "Время окончания события", required = true) @RequestParam LocalDateTime endTime,
            @Parameter(description = "Аннотация события") @RequestParam(required = false) String annotation,
            @Parameter(description = "Заметки к событию") @RequestParam(required = false) String notes,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        eventService.createEvent(title, startTime, endTime, annotation, notes, null, user);
        return "redirect:/dashboard";
    }

    /**
     * Добавление задачи.
     */
    @PostMapping("/tasks")
    @Operation(summary = "Создание новой задачи",
            description = "Создает новую задачу для пользователя")
    public String createTask(
            @Parameter(description = "Название задачи", required = true) @RequestParam String title,
            @Parameter(description = "Описание задачи") @RequestParam(required = false) String description,
            @Parameter(description = "Приоритет задачи") @RequestParam Task.TaskPriority priority,
            @Parameter(description = "Срок выполнения задачи") @RequestParam(required = false) LocalDateTime dueDate,
                             Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskService.createTask(title, description, priority, dueDate, user);
        return "redirect:/dashboard";
    }

    /**
     * Завершение задачи.
     */
    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "Отметка задачи как выполненной",
            description = "Отмечает задачу как выполненную по ID")
    public String completeTask(
            @Parameter(description = "ID задачи", required = true) @PathVariable Long taskId) {
        taskService.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);
        return "redirect:/dashboard";
    }
}
