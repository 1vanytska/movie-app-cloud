package com.project.movieapi.emailservice.service;

import com.project.movieapi.emailservice.model.EmailLog;
import com.project.movieapi.emailservice.repository.EmailLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final EmailLogRepository repository;

    public void processEmail(String to, String subject, String content) {
        EmailLog emailLog = new EmailLog();
        emailLog.setRecipient(to);
        emailLog.setSubject(subject);
        emailLog.setContent(content);
        emailLog.setStatus("PENDING");
        emailLog.setAttemptCount(0);
        emailLog.setLastAttemptTime(LocalDateTime.now());

        emailLog = repository.save(emailLog);

        trySend(emailLog);
    }

    public void trySend(EmailLog emailLog) {
        emailLog.setAttemptCount(emailLog.getAttemptCount() + 1);
        emailLog.setLastAttemptTime(LocalDateTime.now());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailLog.getRecipient());
            message.setSubject(emailLog.getSubject());
            message.setText(emailLog.getContent());
            message.setFrom("noreply@moviereviews.com");

            javaMailSender.send(message);

            emailLog.setStatus("SENT");
            emailLog.setErrorMessage(null);
            log.info("Email sent to {}", emailLog.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            emailLog.setStatus("FAILED");
            emailLog.setErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        repository.save(emailLog);
    }
}