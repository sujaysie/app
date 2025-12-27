package com.example.paymentservice.controller;

import com.example.paymentservice.dto.InitiatePaymentDto;
import com.example.paymentservice.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @PostMapping
    public ResponseEntity<String> initiatePayment(@RequestBody InitiatePaymentDto initiatePaymentDto){
        return new ResponseEntity<>(paymentService.getPaymentLink(initiatePaymentDto.getAmount(),
                initiatePaymentDto.getOrderId(),
                initiatePaymentDto.getName(),
                initiatePaymentDto.getPhone()), HttpStatus.ACCEPTED);
    }
}
