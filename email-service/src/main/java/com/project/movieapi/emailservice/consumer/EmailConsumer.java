package com.project.movieapi.emailservice.consumer;

import com.project.movieapi.emailservice.dto.EmailRequestDto;
import com.project.movieapi.emailservice.service.EmailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailSenderService emailService;

    @RabbitListener(queues = "email_queue")
    public void consumeMessage(EmailRequestDto message) {
        emailService.processEmail(message.getRecipient(), message.getSubject(), message.getBody());
    }
}