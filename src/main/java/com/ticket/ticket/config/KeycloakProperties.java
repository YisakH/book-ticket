package com.ticket.ticket.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String authServerUrl;
    private String realm;
    private String resource;
    private Credentials credentials;
    private String sslRequired;

    // Credentials 클래스를 사용해 시크릿 키 관리
    @Setter
    @Getter
    public static class Credentials {
        private String secret;

    }
}
