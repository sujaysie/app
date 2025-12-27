package com.example.emailservice.utils;

import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;


@Component
public class EmailSession {

    public static Session createGmailSession(String email, String password) {
        Properties props = new Properties();
        props.put(Constants.MAIL_SMTP_HOST_KEY.getValue(), Constants.SMTP_HOST_VALUE.getValue());
        props.put(Constants.MAIL_SMTP_AUTH_KEY, Constants.SMTP_AUTH_VALUE.getValue());
        props.put(Constants.MAIL_SMTP_PORT_KEY, Constants.SMTP_PORT_VALUE.getValue());
        props.put(Constants.MAIL_SMTP_STARTTLS_KEY, Constants.SMTP_STARTTLS_ENABLE_VALUE);

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        return Session.getInstance(props, authenticator);
    }
}