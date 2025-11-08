package io.mkalugin.university.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для отправки писем.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Path EMAILS_DIR = Paths.get("emails");

    /**
     * Отправить письмо на почту.
     *
     * @param to адресат
     * @param subject тема
     * @param text содержание
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Сохранить письмо в директории проекта.
     *
     * @param to адресат
     * @param subject тема
     * @param text содержание
     */
    public void sendEmailMock(String to, String subject, String text) {
        try {
            if (!Files.exists(EMAILS_DIR)) {
                Files.createDirectories(EMAILS_DIR);
            }

            String fileName = "email_" + System.currentTimeMillis() + ".txt";
            Path filePath = EMAILS_DIR.resolve(fileName);

            String content = String.format(
                    "TO: %s%nSUBJECT: %s%n%n%s",
                    to, subject, text
            );

            Files.writeString(filePath, content);
            log.info("Email saved locally: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save email: {}", e.getMessage(), e);
        }
    }
}
