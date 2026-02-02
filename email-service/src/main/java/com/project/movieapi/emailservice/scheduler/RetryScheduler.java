package com.project.movieapi.emailservice.scheduler;

import com.project.movieapi.emailservice.model.EmailLog;
import com.project.movieapi.emailservice.repository.EmailLogRepository;
import com.project.movieapi.emailservice.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RetryScheduler {

    private final EmailLogRepository repository;
    private final EmailSenderService emailService;

    private static final int MAX_ATTEMPTS = 10;

    @Scheduled(fixedRate = 300000)
    public void retryFailedEmails() {
        log.info("Starting retry job...");
        List<EmailLog> failedEmails = repository.findByStatus("FAILED");

        for (EmailLog email : failedEmails) {
            if (email.getAttemptCount() >= MAX_ATTEMPTS) {
                log.warn("Email id: {} reached max attempts ({}). Marking as CANCELLED.", email.getId(), MAX_ATTEMPTS);
                email.setStatus("CANCELLED");
                email.setErrorMessage("Max retry attempts reached. Stopping.");
                repository.save(email);
                continue;
            }

            log.info("Retrying email id: {}, attempt: {}", email.getId(), email.getAttemptCount() + 1);
            emailService.trySend(email);
        }
    }
}