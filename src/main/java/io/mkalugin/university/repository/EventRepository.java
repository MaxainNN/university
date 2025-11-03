package io.mkalugin.university.repository;

import io.mkalugin.university.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для таблицы "events".
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Получение списка событий по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список событий пользователя
     */
    List<Event> findByUserIdOrderByStartTime(Long userId);

    /**
     * Получение списка событий по идентификатору пользователя
     * и по временому промежутку.
     *
     * @param userId идентификатор пользователя
     * @param start дата начала
     * @param end дата окончания
     * @return список событий пользователя
     */
    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.startTime BETWEEN :start AND :end ORDER BY e.startTime")
    List<Event> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);
}
