package com.ticket.ticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final KeycloakProperties keycloakProperties;

    @Autowired
    public SecurityConfig(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String url = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm();
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (람다 표현식 사용)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login/*").permitAll() // /login 경로는 인증 없이 접근 허용
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri(url + "/protocol/openid-connect/certs")
                        )
                );

        return http.build();
    }

    // 세션 인증 전략 설정
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }
}
