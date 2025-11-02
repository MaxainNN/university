package io.mkalugin.university.repository;

import io.mkalugin.university.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserIdOrderByStartTime(Long userId);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.startTime BETWEEN :start AND :end ORDER BY e.startTime")
    List<Event> findByUserIdAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

    List<Event> findByReminderTimeBeforeAndReminderSentFalse(LocalDateTime time);
}
