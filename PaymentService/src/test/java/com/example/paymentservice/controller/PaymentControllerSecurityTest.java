package com.example.paymentservice.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.paymentservice.config.SecurityConfig;
import com.example.paymentservice.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void initiatePaymentReturns401WithoutToken() throws Exception {
        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000,\"orderId\":\"O-1\",\"name\":\"A\",\"phone\":\"9999\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void initiatePaymentReturns403WithoutExpectedRole() throws Exception {
        when(paymentService.getPaymentLink(anyLong(), anyString(), anyString(), anyString())).thenReturn("ok");

        mockMvc.perform(post("/payment")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_GUEST")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000,\"orderId\":\"O-1\",\"name\":\"A\",\"phone\":\"9999\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void initiatePaymentReturns202ForUserRole() throws Exception {
        when(paymentService.getPaymentLink(anyLong(), anyString(), anyString(), anyString())).thenReturn("ok");

        mockMvc.perform(post("/payment")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000,\"orderId\":\"O-1\",\"name\":\"A\",\"phone\":\"9999\"}"))
                .andExpect(status().isAccepted());
    }
}
