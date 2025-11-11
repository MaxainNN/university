package io.mkalugin.university.controller.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mkalugin.university.dto.EventCreateRequest;
import io.mkalugin.university.dto.TaskCreateRequest;
import io.mkalugin.university.dto.TaskResponse;
import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.entity.documents.EventDocument;
import io.mkalugin.university.enums.TaskStatus;
import io.mkalugin.university.exception.UserNotFoundException;
import io.mkalugin.university.service.EventService;
import io.mkalugin.university.service.TaskService;
import io.mkalugin.university.service.UserService;
import io.mkalugin.university.service.search.EventSearchService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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

        Page<TaskResponse> taskPage = taskService.getTasksByUser(user, page, 10);

        List<TaskResponse> activeTasks = taskPage.getContent().stream()
                .filter(task -> !"COMPLETED".equalsIgnoreCase(task.getStatus().name()))
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("tasks", activeTasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        List<Task> overdueTasks = taskService.getOverdueTasks(user.getId());
        model.addAttribute("overdueTasks", overdueTasks);

        return "dashboard";
    }

    /**
     * Календарь пользователя.
     */
    @GetMapping(value = "/calendar", produces = MediaType.TEXT_HTML_VALUE)
    public String calendar(@RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           Authentication authentication,
                           Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        LocalDateTime end = lastDayOfMonth.plusDays(1).atStartOfDay();

        List<Event> events = eventService.getUserEventsByDateRange(user.getId(), start, end);

        Map<Integer, List<Event>> eventsByDay = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartTime().getDayOfMonth()));

        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        List<LocalDate> availableMonths = new ArrayList<>();
        LocalDate startMonth = LocalDate.now().minusMonths(6);
        LocalDate endMonth = LocalDate.now().plusMonths(6);

        LocalDate currentMonth = startMonth.withDayOfMonth(1);
        while (!currentMonth.isAfter(endMonth)) {
            availableMonths.add(currentMonth);
            currentMonth = currentMonth.plusMonths(1);
        }

        Map<String, Object> eventsForJson = new HashMap<>();
        for (Map.Entry<Integer, List<Event>> entry : eventsByDay.entrySet()) {
            List<Map<String, Object>> dayEvents = entry.getValue().stream()
                    .map(event -> {
                        Map<String, Object> eventMap = new HashMap<>();
                        eventMap.put("title", event.getTitle());
                        eventMap.put("annotation", event.getAnnotation());
                        eventMap.put("notes", event.getNotes());
                        eventMap.put("startTime", event.getStartTime().toString());
                        eventMap.put("endTime", event.getEndTime().toString());
                        return eventMap;
                    })
                    .collect(Collectors.toList());
            eventsForJson.put(String.valueOf(entry.getKey()), dayEvents);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String eventsJson = objectMapper.writeValueAsString(eventsForJson);
            model.addAttribute("eventsJson", eventsJson);
        } catch (JsonProcessingException e) {
            model.addAttribute("eventsJson", "{}");
        }

        model.addAttribute("user", user);
        model.addAttribute("eventsByDay", eventsByDay);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedDateJs", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        model.addAttribute("previousMonth", date.minusMonths(1));
        model.addAttribute("nextMonth", date.plusMonths(1));
        model.addAttribute("firstDayOfWeek", firstDayOfWeek);
        model.addAttribute("daysInMonth", date.lengthOfMonth());
        model.addAttribute("availableMonths", availableMonths);

        return "calendar";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, HttpSession session, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime loginTime = (LocalDateTime) session.getAttribute("loginTime");
        if (loginTime == null) {
            loginTime = LocalDateTime.now();
            session.setAttribute("loginTime", loginTime);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        long durationHours = java.time.Duration.between(loginTime, currentTime).toHours();

        model.addAttribute("user", user);
        model.addAttribute("loginTime", loginTime);
        model.addAttribute("currentTime", currentTime);
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("loginDuration", durationHours);

        return "profile";
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
     * Форма создания события
     */
    @GetMapping("/calendar/create")
    public String showCreateEventForm(@RequestParam(required = false)
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                      Authentication authentication,
                                      Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        EventCreateRequest eventRequest = new EventCreateRequest();

        model.addAttribute("user", user);
        model.addAttribute("eventRequest", eventRequest);

        return "create_event";
    }

    /**
     * Обработка создания события
     */
    @PostMapping("/calendar/create")
    public String createEvent(@ModelAttribute EventCreateRequest eventRequest,
                              Authentication authentication,
                              Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        try {
            Event createdEvent = eventService.createEvent(eventRequest);
            return "redirect:/dashboard/calendar?date=" +
                    createdEvent.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(UserNotFoundException::new);

            model.addAttribute("user", user);
            model.addAttribute("error", "Error creating event: " + e.getMessage());
            model.addAttribute("eventRequest", eventRequest);
            return "create_event";
        }
    }

    /**
     * Добавление события.
     */
    @PostMapping("/events")
    public String createEvent(@ModelAttribute EventCreateRequest request) {
        eventService.createEvent(request);
        return "redirect:/dashboard";
    }

    /**
     * Добавление задачи.
     */
    @PostMapping("/tasks")
    public String createTask(@ModelAttribute TaskCreateRequest request) {
        taskService.createTask(request);
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
