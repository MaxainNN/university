package io.mkalugin.university.service;

import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с событиями.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Создание события.
     *
     * @param title заголовок
     * @param startTime дата начала
     * @param endTime дата окончания
     * @param annotation аннотация
     * @param notes заметка
     * @param reminderTime время напоминания
     * @param user пользователь
     * @return созданная сущность события
     */
    public Event createEvent(String title, LocalDateTime startTime, LocalDateTime endTime,
                             String annotation, String notes, LocalDateTime reminderTime, User user) {
        Event event = new Event(title, startTime, endTime, user);
        event.setAnnotation(annotation);
        event.setNotes(notes);
        event.setReminderTime(reminderTime);
        return eventRepository.save(event);
    }

    /**
     * Получение списка событий по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список событий пользователя
     */
    public List<Event> getUserEvents(Long userId) {
        return eventRepository.findByUserIdOrderByStartTime(userId);
    }

    /**
     * Получение списка событий по временому промежутку.
     *
     * @param userId идентификатор пользователя
     * @param start дата начала
     * @param end дата окончания
     * @return список событий пользователя
     */
    public List<Event> getUserEventsByDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByUserIdAndDateRange(userId, start, end);
    }

    /**
     * Обновление события.
     *
     * @param eventId идентификатор события
     * @param startTime дата начала
     * @param endTime дата окончания
     * @param annotation аннотация
     * @param notes заметка
     * @param reminderTime время напоминания
     * @return обвновленная сущность события
     */
    public Event updateEvent(Long eventId, String title, LocalDateTime startTime, LocalDateTime endTime,
                             String annotation, String notes, LocalDateTime reminderTime) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(title);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setAnnotation(annotation);
        event.setNotes(notes);
        event.setReminderTime(reminderTime);

        return eventRepository.save(event);
    }
}
