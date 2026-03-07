package com.store.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.userservice.dtos.TokenResponse;
import com.store.userservice.models.User;
import com.store.userservice.repo.UserRepository;
import com.store.userservice.services.TokenIssuanceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final TokenIssuanceService tokenIssuanceService;
    private final ObjectMapper objectMapper;

    public OAuth2LoginSuccessHandler(UserRepository userRepository,
                                     TokenIssuanceService tokenIssuanceService,
                                     ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.tokenIssuanceService = tokenIssuanceService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid OAuth2 principal");
            return;
        }

        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email claim missing");
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServletException("OAuth user was not persisted"));

        TokenResponse tokenResponse = tokenIssuanceService.issueTokens(user);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), tokenResponse);
    }
}
