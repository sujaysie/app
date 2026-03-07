package com.store.userservice.controller;

import com.store.userservice.services.JwkService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwkController {
    private final JwkService jwkService;

    public JwkController(JwkService jwkService) {
        this.jwkService = jwkService;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        return jwkService.getPublicJwkSet().toJSONObject();
    }
}
