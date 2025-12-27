package com.example.paymentservice.dto;

import lombok.Data;

@Data
public class InitiatePaymentDto {
    private Long amount;
    private String orderId;
    private String name;
    private String phone;
}
