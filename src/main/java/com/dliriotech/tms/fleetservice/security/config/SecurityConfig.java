package com.dliriotech.tms.fleetservice.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ServiceAuthFilter serviceAuthFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/openapi.yaml").permitAll()
                        .pathMatchers("/api/v1/equipos/**").permitAll()
                        .pathMatchers("/api/v1/observaciones-equipo/**").permitAll()
                        .pathMatchers("/api/v1/catalogos/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(serviceAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.fromRunnable(() ->
                                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                                )
                        )
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.fromRunnable(() ->
                                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
                                )
                        )
                )
                .build();
    }
}