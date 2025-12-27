package com.example.emailservice.utils;

public enum Constants {
    // Mail Property Keys
    MAIL_SMTP_HOST_KEY("mail.smtp.host"),
    MAIL_SMTP_PORT_KEY("mail.smtp.port"),
    MAIL_SMTP_AUTH_KEY("mail.smtp.auth"),
    MAIL_SMTP_STARTTLS_KEY("mail.smtp.starttls.enable"),

    // Mail Property Values
    SMTP_HOST_VALUE("smtp.gmail.com"),
    SMTP_AUTH_VALUE("mail.smtp.auth"), // Note: This key is duplicated, consider renaming or grouping
    SMTP_PORT_VALUE("587"),
    SMTP_STARTTLS_ENABLE_VALUE("true");

    private final String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
