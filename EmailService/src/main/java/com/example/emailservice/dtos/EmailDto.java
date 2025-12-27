package com.example.emailservice.dtos;

import lombok.Data;

@Data
public class EmailDto {
    String to;
    String from;
    String subject;
    String body;
}
