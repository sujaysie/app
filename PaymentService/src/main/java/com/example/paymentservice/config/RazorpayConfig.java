package com.example.paymentservice.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class RazorpayConfig {
    @Value("$(razorpay.id)")
    private String razorpayId;
    @Value("$(razorpay.secret)")
    private String razorpaySecret;
    @Bean
    public RazorpayClient getRazorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayId,razorpaySecret);
    }
}
