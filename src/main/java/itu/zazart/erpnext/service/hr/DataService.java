package itu.zazart.erpnext.service.hr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public DataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String deleteAll(String sid) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            String baseUrl = erpnextApiUrl + "/api/method/erpnext.utilities.fonction.reset_data";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error while deleting data", e);
            throw new RuntimeException("Error during reset api in java");
        }
    }

}
