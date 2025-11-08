package io.mkalugin.university.repository;

import io.mkalugin.university.entity.Task;
import io.mkalugin.university.entity.User;
import io.mkalugin.university.enums.TaskPriority;
import io.mkalugin.university.enums.TaskStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Тест на {@link TaskRepository}
 *
 * <p> Репозиторий с методами для
 * работы с задачами </p>
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Task repository tests")
class TaskRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(TaskRepositoryTest.class);

    @Mock
    private TaskRepository taskRepository;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        return user;
    }

    private Task createTask(Long id, String title, User user, TaskStatus status, TaskPriority priority,
                            LocalDateTime dueDate) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setUser(user);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    @Test
    @Order(1)
    @DisplayName("Find All By user works")
    void findAllByUserWithPaginationShouldReturnPageOfTasks() {
        log.info("findAllByUserWithPaginationShouldReturnPageOfTasks test started");

        User user = createUser(1L);
        Pageable pageable = PageRequest.of(0, 10);

        Task task1 = createTask(1L, "Task 1", user, TaskStatus.PENDING, TaskPriority.HIGH,
                LocalDateTime.now().plusDays(1));
        Task task2 = createTask(2L, "Task 2", user, TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2));

        List<Task> tasks = Arrays.asList(task1, task2);
        Page<Task> expectedPage = new PageImpl<>(tasks, pageable, tasks.size());

        when(taskRepository.findAllByUser(user, pageable)).thenReturn(expectedPage);

        Page<Task> result = taskRepository.findAllByUser(user, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Task::getId)
                .containsExactly(1L, 2L);
        assertThat(result.getContent()).extracting(Task::getTitle)
                .containsExactly("Task 1", "Task 2");

        log.info("findAllByUserWithPaginationShouldReturnPageOfTasks test finished");
    }

    @Test
    @Order(2)
    @DisplayName("Find by user and status works")
    void findByUserIdAndStatusWithValidParamsShouldReturnTasks() {
        log.info("findByUserIdAndStatusWithValidParamsShouldReturnTasks test started");

        Long userId = 1L;
        TaskStatus status = TaskStatus.PENDING;

        Task task1 = createTask(1L, "Task 1", createUser(userId), status, TaskPriority.HIGH,
                LocalDateTime.now().plusDays(1));
        Task task2 = createTask(2L, "Task 2", createUser(userId), status, TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2));

        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(taskRepository.findByUserIdAndStatus(userId, status)).thenReturn(expectedTasks);

        List<Task> result = taskRepository.findByUserIdAndStatus(userId, status);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getStatus)
                .containsOnly(status);

        log.info("findByUserIdAndStatusWithValidParams test finished");
    }

    @Test
    @Order(3)
    @DisplayName("Find by user and priority works")
    void findByUserIdAndPriorityWithValidParamsShouldReturnTasks() {
        log.info("findByUserIdAndPriorityWithValidParamsShouldReturnTasks test started");

        Long userId = 1L;
        TaskPriority priority = TaskPriority.HIGH;

        Task task1 = createTask(1L, "Task 1", createUser(userId), TaskStatus.PENDING, priority,
                LocalDateTime.now().plusDays(1));
        Task task2 = createTask(2L, "Task 2", createUser(userId), TaskStatus.IN_PROGRESS, priority,
                LocalDateTime.now().plusDays(2));

        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(taskRepository.findByUserIdAndPriority(userId, priority)).thenReturn(expectedTasks);

        List<Task> result = taskRepository.findByUserIdAndPriority(userId, priority);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getPriority)
                .containsOnly(priority);

        log.info("findByUserIdAndPriorityWithValidParams test finished");
    }

    @Test
    @Order(4)
    @DisplayName("Find overdue tasks works")
    void findOverdueTasksWithOverdueTasksShouldReturnOverdueTasks() {
        log.info("findOverdueTasksWithOverdueTasksShouldReturnTasks test started");

        Long userId = 1L;

        Task overdueTask1 = createTask(1L, "Overdue Task 1", createUser(userId),
                TaskStatus.PENDING, TaskPriority.HIGH,
                LocalDateTime.now().minusDays(1));
        Task overdueTask2 = createTask(2L, "Overdue Task 2", createUser(userId),
                TaskStatus.IN_PROGRESS, TaskPriority.MEDIUM,
                LocalDateTime.now().minusHours(2));

        List<Task> expectedTasks = Arrays.asList(overdueTask1, overdueTask2);

        when(taskRepository.findOverdueTasks(userId)).thenReturn(expectedTasks);

        List<Task> result = taskRepository.findOverdueTasks(userId);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getDueDate)
                .allSatisfy(dueDate -> assertThat(dueDate).isBefore(LocalDateTime.now()));
        assertThat(result).extracting(Task::getStatus)
                .noneMatch(status -> status == TaskStatus.COMPLETED);

        log.info("findOverdueTasksWithOverdueTasks test finished");
    }

    @Test
    @Order(5)
    @DisplayName("Find by priority and status works")
    void findByUserIdAndStatusAndPriorityWithValidParamsShouldReturnTasks() {
        log.info("findByUserIdAndStatusAndPriorityWithValidParamsShouldReturnTasks test started");

        Long userId = 1L;
        TaskStatus status = TaskStatus.PENDING;
        TaskPriority priority = TaskPriority.HIGH;

        Task task1 = createTask(1L, "Task 1", createUser(userId), status, priority,
                LocalDateTime.now().plusDays(1));
        Task task2 = createTask(2L, "Task 2", createUser(userId), status, priority,
                LocalDateTime.now().plusDays(2));

        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority))
                .thenReturn(expectedTasks);

        List<Task> result = taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getStatus)
                .containsOnly(status);
        assertThat(result).extracting(Task::getPriority)
                .containsOnly(priority);

        log.info("findByUserIdAndStatusAndPriorityWithValidParams test finished");
    }

    @Test
    @Order(6)
    @DisplayName("Find by user and status with no tasks works")
    void findByUserIdAndStatusWithNoTasksShouldReturnEmptyList() {
        log.info("findByUserIdAndStatusWithNoTasksShouldReturnEmptyList test started");

        Long userId = 1L;
        TaskStatus status = TaskStatus.COMPLETED;

        when(taskRepository.findByUserIdAndStatus(userId, status)).thenReturn(List.of());

        List<Task> result = taskRepository.findByUserIdAndStatus(userId, status);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        log.info("findByUserIdAndStatusWithNoTasksShouldReturnEmptyList test finished");
    }

    @Test
    @Order(7)
    @DisplayName("Find overdue tasks with no tasks works")
    void findOverdueTasksWithNoOverdueTasksShouldReturnEmptyList() {
        log.info("findOverdueTasksWithNoTasksShouldReturnEmptyList test started");

        Long userId = 1L;

        when(taskRepository.findOverdueTasks(userId)).thenReturn(List.of());

        List<Task> result = taskRepository.findOverdueTasks(userId);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        log.info("findOverdueTasksWithNoTasksShouldReturnEmptyList test finished");
    }
}
