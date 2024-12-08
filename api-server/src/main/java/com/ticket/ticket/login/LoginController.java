package com.ticket.ticket.login;

import com.ticket.ticket.config.KeycloakProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

        try {
            ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            }else{
                throw new HttpClientErrorException(response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // 더 자세한 오류 메시지 출력
            return ResponseEntity.status(e.getStatusCode()).body("Failed to retrieve token: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve token: " + e.getMessage());
        }
    }
}
