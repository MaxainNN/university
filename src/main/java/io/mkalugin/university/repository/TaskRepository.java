package io.mkalugin.university.repository;

import io.mkalugin.university.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status);
    List<Task> findByUserIdAndPriority(Long userId, Task.TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < CURRENT_TIMESTAMP AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(Long userId);

    List<Task> findByUserIdAndStatusIn(Long userId, List<Task.TaskStatus> statuses);
}
