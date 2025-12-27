package com.example.paymentservice.services;

import com.example.paymentservice.paymentgateway.PaymentGateway;
import com.example.paymentservice.paymentgateway.PaymentGatewayStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    PaymentGatewayStrategy paymentGatewayStrategy;
    public String getPaymentLink(Long amount,String orderId, String name, String phone){
        var pg = paymentGatewayStrategy.getPaymentGateway();
        return pg.getPaymentLink(amount,orderId,name,phone);
    }
}
