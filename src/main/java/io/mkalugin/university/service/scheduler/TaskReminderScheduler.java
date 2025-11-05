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

    // Раз в час
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void sendTaskReminders() {
        log.info("Running task reminder scheduler");

        userRepository.findAll().forEach(user -> {
            if (user.getEmail() == null || user.getEmail().isBlank()) return;

//            var pendingTasks = taskRepository.findByUserAndStatusNot(user, "COMPLETED");
//            if (pendingTasks.isEmpty()) return;

            StringBuilder sb = new StringBuilder("Привет, " + user.getUsername() + "!\n\n");
            sb.append("Твои незавершённые задачи:\n");
//            pendingTasks.forEach(task -> sb.append("• ").append(task.getTitle())
//                    .append(task.getDueDate() != null ? " (до " + task.getDueDate() + ")" : "")
//                    .append("\n"));
            sb.append("\nУспей завершить их!");

            emailService.sendEmail(user.getEmail(), "Напоминание о задачах", sb.toString());
        });
    }
}
