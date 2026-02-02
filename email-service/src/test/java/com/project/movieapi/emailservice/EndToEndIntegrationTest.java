package com.project.movieapi.emailservice;

import com.project.movieapi.emailservice.dto.EmailRequestDto;
import com.project.movieapi.emailservice.model.EmailLog;
import com.project.movieapi.emailservice.repository.EmailLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Disabled
class EndToEndIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EmailLogRepository repository;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldConsumeMessage_SaveToElastic_AndSendEmail() {
        EmailRequestDto request = new EmailRequestDto("integration@test.com", "Integration Subject", "Testing Body");

        rabbitTemplate.convertAndSend("email_queue", request);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            List<EmailLog> logs = repository.findByStatus("SENT");
            assertThat(logs).hasSize(1);

            EmailLog log = logs.get(0);
            assertThat(log.getRecipient()).isEqualTo("integration@test.com");
            assertThat(log.getSubject()).isEqualTo("Integration Subject");
        });

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}