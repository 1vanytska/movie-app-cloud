package com.project.movieapi.moviegateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
                http
                                .authorizeExchange(exchange -> exchange
                                                .pathMatchers(org.springframework.http.HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/reviews/**", "/reviews/**")
                                                .permitAll()
                                                .anyExchange().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .authenticationSuccessHandler(
                                                                new RedirectServerAuthenticationSuccessHandler(
                                                                                "http://34.79.215.93.nip.io")))
                                .csrf(ServerHttpSecurity.CsrfSpec::disable);

                return http.build();
        }
}