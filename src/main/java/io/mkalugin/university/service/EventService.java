package io.mkalugin.university.service;

import io.mkalugin.university.dto.EventCreateRequest;
import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.exception.UserNotFoundException;
import io.mkalugin.university.mapper.EventMapper;
import io.mkalugin.university.repository.EventRepository;
import io.mkalugin.university.repository.UserRepository;
import io.mkalugin.university.service.search.EventSearchService;
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
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final EventSearchService eventSearchService;

    /**
     * Создание события.
     *
     * @param request запрос на создание события
     * @return созданная сущность события
     */
    public Event createEvent(EventCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Event event = eventMapper.toEntity(request, user);
        Event saved = eventRepository.save(event);
        eventSearchService.indexEvent(saved);

        return saved;
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
}
