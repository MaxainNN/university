package io.mkalugin.university.controller.api;

import io.mkalugin.university.dto.EventCreateRequest;
import io.mkalugin.university.entity.Event;
import io.mkalugin.university.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер с методами для работы с событиями.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Events API", description = "REST API для работы с событиями")
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;

    /**
     * Создание события
     *
     * @param request запрос с данными для создания события
     * @return ответ с данными созданного события
     */
    @PostMapping("/events")
    @Operation(
            summary = "Создание нового события",
            description = "Создает событие для указанного пользователя"
    )
    public ResponseEntity<Event> createEvent(
            @RequestBody @Valid EventCreateRequest request) {
        Event event = eventService.createEvent(request);
        return ResponseEntity.ok(event);
    }

    /**
     * Получение списка событий текущего пользователя
     * по временному промежутку
     *
     * @param userId Идентификатор пользователя
     * @param start Дата начала
     * @param end Дата окончания
     * @return список событий пользователя
     */
    @GetMapping("/events/{userId}")
    @Operation(
            summary = "Получение событий пользователя по временному промежутку",
            description = "Возвращает список событий пользователя в указанном временном диапазоне"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список событий успешно получен"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры даты")
    })
    public ResponseEntity<List<Event>> getUserEventsByDateRange(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Дата начала периода (формат: yyyy-MM-dd'T'HH:mm:ss)",
                    example = "2025-11-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Дата окончания периода (формат: yyyy-MM-dd'T'HH:mm:ss)",
                    example = "2025-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<Event> events = eventService.getUserEventsByDateRange(userId, start, end);
        return ResponseEntity.ok(events);
    }

    /**
     * Получение списка событий текущего пользователя
     * начиная с текщей даты до указанное
     * количество дней.
     *
     * @param userId Идентификатор пользователя
     * @param daysAhead кол-во дней для поиска вперед
     * @return список событий пользователя
     */
    @GetMapping("/events/{userId}/upcoming")
    @Operation(
            summary = "Получение предстоящих событий пользователя",
            description = "Возвращает список событий пользователя начиная с текущей даты"
    )
    public ResponseEntity<List<Event>> getUpcomingUserEvents(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "Количество дней для поиска вперед", example = "30")
            @RequestParam(defaultValue = "30") int daysAhead) {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(daysAhead);

        List<Event> events = eventService.getUserEventsByDateRange(userId, start, end);
        return ResponseEntity.ok(events);
    }
}
