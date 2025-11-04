package io.mkalugin.university.service.search;

import io.mkalugin.university.entity.Event;
import io.mkalugin.university.entity.documents.EventDocument;
import io.mkalugin.university.repository.search.EventSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для поиска событий.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventSearchService {

    private final EventSearchRepository eventSearchRepository;

    /**
     * Идексирование события
     *
     * @param event сущность события
     */
    public void indexEvent(Event event) {
        EventDocument doc = EventDocument.builder()
                .id(String.valueOf(event.getId()))
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .notes(event.getNotes())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .userId(event.getUser().getId())
                .build();
        eventSearchRepository.save(doc);
        log.info("Indexed event {} in Elasticsearch", event.getId());
    }

    /**
     * Поиск событий по заголовку
     *
     * @param query поисковой запрос
     * @return список событий
     */
    public List<EventDocument> searchByTitle(String query) {
        return eventSearchRepository.findByTitleContainingIgnoreCase(query);
    }
}
