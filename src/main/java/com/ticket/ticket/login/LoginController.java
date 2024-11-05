package com.ticket.ticket.login;

import com.ticket.ticket.config.KeycloakProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequestMapping("/login")
@Controller
public class LoginController {

    private final KeycloakProperties keycloakProperties;

    @Autowired
    public LoginController(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestParam String userName, @RequestParam String password) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String keycloakUrl = keycloakProperties.getAuthServerUrl() +
                "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", keycloakProperties.getResource());
        params.add("client_secret", keycloakProperties.getCredentials().getSecret());
        params.add("grant_type", "password");
        params.add("username", userName);
        params.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        System.out.println(keycloakUrl);

        try {
            ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.POST, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            // 더 자세한 오류 메시지 출력
            System.err.println("Error response body: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("Failed to retrieve token: " + e.getResponseBodyAsString());
        }
    }
}
