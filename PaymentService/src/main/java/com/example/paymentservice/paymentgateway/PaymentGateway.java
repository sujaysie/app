package com.example.paymentservice.paymentgateway;

public interface PaymentGateway {

    String getPaymentLink(Long amount, String orderId, String name, String phoneNumber);
}
