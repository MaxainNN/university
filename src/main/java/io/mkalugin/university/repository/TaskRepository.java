package io.mkalugin.university.repository;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для таблицы "tasks".
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Получение списка задач пользователя с пагинацией.
     *
     * @param user пользователь
     * @param pageable объект типа Pageable
     * @return список задач пользователя
     */
    Page<Task> findAllByUser(User user, Pageable pageable);

    /**
     * Получение списка задач по статусу.
     *
     * @param userId идентификатор пользователя
     * @param status статус
     * @return список задач
     */
    List<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status);

    /**
     * Получение списка задач по приоритету.
     *
     * @param userId идентификатор пользователя
     * @param priority приоритет
     * @return список задач
     */
    List<Task> findByUserIdAndPriority(Long userId, Task.TaskPriority priority);

    /**
     * Получение списка актуальных задач.
     *
     * @param userId идентификатор пользователя
     * @return список задач
     */
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < CURRENT_TIMESTAMP AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(Long userId);
}
