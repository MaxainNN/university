package io.mkalugin.university.repository.search;

import io.mkalugin.university.entity.documents.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для документа-события
 */
@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {

    /**
     * Поиск событий по заголовку
     *
     * @param title заголовок
     * @return список событий
     */
    List<EventDocument> findByTitleContainingIgnoreCase(String title);
}
