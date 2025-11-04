package io.mkalugin.university.controller.web;

import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.entity.documents.EventDocument;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.exception.UserNotFoundException;
import io.mkalugin.university.service.EventService;
import io.mkalugin.university.service.TaskService;
import io.mkalugin.university.service.UserService;
import io.mkalugin.university.service.search.EventSearchService;
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
 * Web Контроллер с дашбордом.
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EventService eventService;
    private final TaskService taskService;
    private final UserService userService;
    private final EventSearchService eventSearchService;

    /**
     * Главная страница дашборда.
     */
    @GetMapping
    public String dashboard(
            Authentication authentication,
            Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        Page<Task> taskPage = taskService.getTasksByUser(user, page, 10);

        model.addAttribute("user", user);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        List<Task> overdueTasks = taskService.getOverdueTasks(user.getId());
        model.addAttribute("overdueTasks", overdueTasks);

        return "dashboard";
    }

    /**
     * Календарь пользователя.
     */
    @GetMapping("/calendar")
    public String calendar(@RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Event> events = eventService.getUserEventsByDateRange(user.getId(), start, end);

        Map<Integer, List<Event>> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartTime().getDayOfMonth()));

        model.addAttribute("user", user);
        model.addAttribute("eventsByDay", eventsByDay);
        model.addAttribute("selectedDate", date);

        return "calendar";
    }

    /**
     * Поиск события.
     */
    @GetMapping("/calendar/search")
    public String searchEvents(@RequestParam("query") String query,
                               Authentication authentication,
                               Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        List<EventDocument> results = eventSearchService.searchByTitle(query)
                .stream()
                .filter(e -> e.getUserId().equals(user.getId()))
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("results", results);
        model.addAttribute("query", query);

        return "calendar_search_results";
    }

    /**
     * Добавление события.
     */
    @PostMapping("/events")
    public String createEvent(
            @RequestParam String title,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime,
            @RequestParam(required = false) String annotation,
            @RequestParam(required = false) String notes,
            Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        eventService.createEvent(title, startTime, endTime, annotation, notes, null, user);
        return "redirect:/dashboard";
    }

    /**
     * Добавление задачи.
     */
    @PostMapping("/tasks")
    public String createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam TaskPriority priority,
            @RequestParam(required = false) LocalDateTime dueDate,
                             Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        taskService.createTask(title, description, priority, dueDate, user);
        return "redirect:/dashboard";
    }

    /**
     * Завершение задачи.
     */
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(@PathVariable Long taskId) {
        taskService.updateTaskStatus(taskId, TaskStatus.COMPLETED);
        return "redirect:/dashboard";
    }
}
