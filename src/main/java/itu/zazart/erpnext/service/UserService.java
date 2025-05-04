package itu.zazart.erpnext.service;

import itu.zazart.erpnext.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Collections;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper(); // pour mapper du JSON

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void checkUser(String name, HttpSession session) {
        String url = erpnextApiUrl + "/api/resource/User/" + name;
        System.out.println("URL finale : " + url);

        String sid = (String) session.getAttribute("erp_sid");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("Réponse brute : " + responseEntity.getBody());

            User user = mapToUser(responseEntity.getBody());
            session.setAttribute("erp_user", user);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Request error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
        }

    }

    private User mapToUser(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode dataNode = root.path("data");

            User user = new User();
            user.setName(dataNode.path("name").asText());
            user.setEmail(dataNode.path("email").asText());
            user.setFirstName(dataNode.path("first_name").asText());
            user.setLastName(dataNode.path("last_name").isNull() ? null : dataNode.path("last_name").asText());
            user.setFullName(dataNode.path("full_name").asText());
            user.setUsername(dataNode.path("username").asText());
            user.setUserImage(dataNode.path("user_image").asText());

            if(user.getUserImage().startsWith("/")){
                user.setUserImage(erpnextApiUrl+user.getUserImage());
            }

            return user;
        } catch (Exception e) {
            logger.error("Error mapping user data: ", e);
            return null;
        }
    }
}
