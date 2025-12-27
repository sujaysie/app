package com.example.paymentservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "webhook")
public class StripeWebhookController {
    @PostMapping
    public void respondToEvent(@RequestBody String event){
        System.out.println(event);
    }
}
