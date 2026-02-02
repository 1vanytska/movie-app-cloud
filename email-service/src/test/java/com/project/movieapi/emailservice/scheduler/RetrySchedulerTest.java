package com.project.movieapi.emailservice.scheduler;

import com.project.movieapi.emailservice.model.EmailLog;
import com.project.movieapi.emailservice.repository.EmailLogRepository;
import com.project.movieapi.emailservice.service.EmailSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrySchedulerTest {

    @Mock
    private EmailLogRepository repository;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private RetryScheduler retryScheduler;

    @Test
    void shouldRetryEmail_whenAttemptsAreBelowMax() {
        EmailLog email = new EmailLog();
        email.setId("1");
        email.setAttemptCount(5);
        email.setStatus("FAILED");

        when(repository.findByStatus("FAILED")).thenReturn(List.of(email));

        retryScheduler.retryFailedEmails();

        verify(emailSenderService, times(1)).trySend(email);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCancelEmail_whenMaxAttemptsReached() {
        EmailLog email = new EmailLog();
        email.setId("2");
        email.setAttemptCount(10);
        email.setStatus("FAILED");

        when(repository.findByStatus("FAILED")).thenReturn(List.of(email));

        retryScheduler.retryFailedEmails();

        verify(emailSenderService, never()).trySend(email);

        verify(repository, times(1)).save(argThat(e ->
                e.getStatus().equals("CANCELLED") &&
                        e.getErrorMessage().contains("Max retry attempts reached")
        ));
    }
}