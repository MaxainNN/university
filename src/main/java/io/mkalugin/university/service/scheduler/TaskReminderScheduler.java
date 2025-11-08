package io.mkalugin.university.service.scheduler;

import io.mkalugin.university.repository.TaskRepository;
import io.mkalugin.university.repository.UserRepository;
import io.mkalugin.university.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Планировщик уведомлений.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskReminderScheduler {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final UserActivityService userActivityService;

    /**
     * Отправление уведомления
     * о задачах пользователя
     */
    @Scheduled(cron = "${scheduler.reminder-cron}")
    public void sendTaskReminders() {
        log.info("Running task reminder scheduler");

        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
        List<String> activeUsernames = userActivityService.getUsersActiveSince(since);

        for (String username : activeUsernames) {
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) continue;
            var user = userOpt.get();
            var overdueTasks = taskRepository.findOverdueTasks(user.getId());
            if (overdueTasks.isEmpty()) continue;

            StringBuilder sb = new StringBuilder("Привет, " + user.getUsername() + "!\n\n");
            sb.append("Твои незавершённые задачи:\n");
            overdueTasks.forEach(task -> sb.append("• ").append(task.getTitle())
                    .append(task.getDueDate() != null ? " (до " + task.getDueDate() + ")" : "")
                    .append("\n"));
            sb.append("\nПостарайся завершить их!");

            emailService.sendEmailMock(user.getEmail(), "Напоминание о задачах", sb.toString());
        }
    }
}
