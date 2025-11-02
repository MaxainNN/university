package io.mkalugin.university.controller;

import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.service.EventService;
import io.mkalugin.university.service.TaskService;
import io.mkalugin.university.service.UserService;
import lombok.RequiredArgsConstructor;
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

/**
 * Контроллер с дашбордом.
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EventService eventService;
    private final TaskService taskService;
    private final UserService userService;

    /**
     * Главная страница дашборда
     */
    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Event> events = eventService.getUserEvents(user.getId());
        List<Task> tasks = taskService.getUserTasks(user.getId());
        List<Task> overdueTasks = taskService.getOverdueTasks(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("tasks", tasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("currentDate", LocalDate.now());

        return "dashboard";
    }

    /**
     * Календарь пользователя
     */
    @GetMapping("/calendar")
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

        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("selectedDate", date);

        return "calendar";
    }

    /**
     * Добавление события
     */
    @PostMapping("/events")
    public String createEvent(@RequestParam String title,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                              @RequestParam(required = false) String annotation,
                              @RequestParam(required = false) String notes,
                              Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        eventService.createEvent(title, startTime, endTime, annotation, notes, null, user);
        return "redirect:/dashboard";
    }

    /**
     * Добавление задачи
     */
    @PostMapping("/tasks")
    public String createTask(@RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam Task.TaskPriority priority,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
                             Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskService.createTask(title, description, priority, dueDate, user);
        return "redirect:/dashboard";
    }

    /**
     * Завершение задачи
     */
    @PostMapping("/tasks/{taskId}/complete")
    public String completeTask(@PathVariable Long taskId) {
        taskService.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);
        return "redirect:/dashboard";
    }
}
