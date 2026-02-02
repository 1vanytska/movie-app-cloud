package com.project.movieapi.springbootrestapi.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDto {
    private String recipient;
    private String subject;
    private String body;
}