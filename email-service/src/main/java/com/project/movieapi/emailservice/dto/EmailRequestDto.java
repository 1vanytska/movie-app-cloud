package com.project.movieapi.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto implements Serializable {
    private String recipient;
    private String subject;
    private String body;
}