package com.project.movieapi.emailservice.service;

import com.project.movieapi.emailservice.model.EmailLog;
import com.project.movieapi.emailservice.repository.EmailLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailLogRepository repository;

    @InjectMocks
    private EmailSenderService emailService;

    @Test
    void processEmail_shouldSendEmailAndSetStatusSent_whenSuccess() {
        String to = "test@example.com";
        String subject = "Hello";
        String content = "World";

        when(repository.save(any(EmailLog.class))).thenAnswer(i -> i.getArguments()[0]);

        emailService.processEmail(to, subject, content);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));

        verify(repository, times(2)).save(argThat(log ->
                (log.getStatus().equals("PENDING") || log.getStatus().equals("SENT"))
        ));
    }

    @Test
    void processEmail_shouldSetStatusFailed_whenMailSenderThrowsException() {
        String to = "fail@example.com";

        when(repository.save(any(EmailLog.class))).thenAnswer(i -> i.getArguments()[0]);

        doThrow(new MailSendException("Server is down"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.processEmail(to, "Subj", "Text");

        verify(repository, atLeastOnce()).save(argThat(log ->
                log.getStatus().equals("FAILED") && log.getErrorMessage().contains("Server is down")
        ));
    }
}