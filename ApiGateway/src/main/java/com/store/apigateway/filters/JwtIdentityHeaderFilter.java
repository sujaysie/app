package com.store.apigateway.filters;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtIdentityHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .flatMap(authentication -> mutateRequest(authentication, exchange, chain))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> mutateRequest(Authentication authentication,
                                     ServerWebExchange exchange,
                                     GatewayFilterChain chain) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return chain.filter(exchange);
        }

        Jwt jwt = jwtAuthenticationToken.getToken();
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        List<String> roles = jwt.getClaimAsStringList("roles");
        String rolesHeader = roles == null ? "" : roles.stream().collect(Collectors.joining(","));

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    if (userId != null && !userId.isBlank()) {
                        headers.set("X-User-Id", userId);
                    }
                    if (email != null && !email.isBlank()) {
                        headers.set("X-User-Email", email);
                    }
                    if (!rolesHeader.isBlank()) {
                        headers.set("X-User-Roles", rolesHeader);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
