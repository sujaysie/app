package com.example.emailservice.services;

import com.example.emailservice.dtos.EmailDto;
import com.example.emailservice.exception.EmailDeliveryException;
import com.example.emailservice.utils.EmailSession;
import com.example.emailservice.utils.EmailUtils;
import org.springframework.stereotype.Service;

import javax.mail.Session;

@Service
public class EmailService {

    /**
     * Sends an email using the provided {@link com.example.emailservice.dtos.EmailDto} object.
     *
     * @param messageDTO the {@link EmailDto} object containing details such as sender's email, recipient's email,
     *                   subject and body.
     * @throws EmailDeliveryException if there is an error while sending the email.
     */
    public void sendEmail(EmailDto messageDTO) throws EmailDeliveryException {
        Session session = EmailSession.createGmailSession(messageDTO.getFrom(), "dummy password");
        EmailUtils.sendEmail(session, messageDTO.getTo(), messageDTO.getSubject(), messageDTO.getBody());
    }
}