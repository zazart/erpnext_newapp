package itu.zazart.erpnext.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;


    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean authenticate(String usr, String pwd, HttpSession session) {
        String url = erpnextApiUrl + "/api/method/login";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("usr", usr);
        body.add("pwd", pwd);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                if (cookies != null) {
                    for (String cookie : cookies) {
                        if (cookie.startsWith("sid=")) {
                            String sid = cookie.split(";")[0].substring(4);
                            session.setAttribute("erp_sid", sid);
                            return true;
                        }
                    }
                }
                return false;
            }
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", usr, e);
        }
        return false;
    }

}
