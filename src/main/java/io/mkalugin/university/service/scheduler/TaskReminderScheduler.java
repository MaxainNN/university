package io.mkalugin.university.service.scheduler;

import io.mkalugin.university.repository.TaskRepository;
import io.mkalugin.university.repository.UserRepository;
import io.mkalugin.university.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Планировщик уведомлений.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskReminderScheduler {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Scheduled(cron = "${scheduler.reminder-cron}")
    public void sendTaskReminders() {
        log.info("Running task reminder scheduler");

        userRepository.findAll().forEach(user -> {

            var overdueTasks = taskRepository.findOverdueTasks(user.getId());
            if (overdueTasks.isEmpty()) return;

            StringBuilder sb = new StringBuilder("Привет, " + user.getUsername() + "!\n\n");
            sb.append("Твои незавершённые задачи:\n");
            overdueTasks.forEach(task -> sb.append("• ").append(task.getTitle())
                    .append(task.getDueDate() != null ? " (до " + task.getDueDate() + ")" : "")
                    .append("\n"));
            sb.append("\nПостарайся завершить их!");

            emailService.sendEmail(user.getEmail(), "Напоминание о задачах", sb.toString());
        });
    }
}
