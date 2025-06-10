package com.example.erpnext.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.erpnext.config.AppConfig;
import com.example.erpnext.modules.FrappeSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class DataService {
    private final AppConfig apiConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public DataService(AppConfig apiConfig, RestTemplate restTemplate) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
    }

    public String deleteAll(FrappeSession frappeSession) {
        try {
            // Construire le corps de la requête
            Map<String, Object> requestBody = new HashMap<>();

            // Authentification avec la session Frappe
            String baseUrl = apiConfig.getFrappeApiUrl() + "/method/erpnext.dev.fonction.reset_data";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + frappeSession.getSid());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            // Appel POST
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'api de reinitialisation dans java");
        }
    }

    


        public String importerAll(String[] pathFile,FrappeSession frappeSession) throws Exception {
        try {
            // Construire le corps de la requête avec les chemins de fichiers
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fichiers_csv", pathFile[0]);
            requestBody.put("fichiers_csv_2", pathFile[1]);
            requestBody.put("fichiers_csv_3", pathFile[2]);

            // Authentification avec la session Frappe
            String baseUrl = apiConfig.getFrappeApiUrl() + "/method/erpnext.dev.fonction.importer";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + frappeSession.getSid());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            // Appel POST
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String body = response.getBody();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode json = mapper.readTree(body);
                    String message = "";
                    if (json.has("message")) {
                        System.out.println("Miditra 2");
                        message = json.get("message").asText();
                    } else if (json.has("_server_messages")) {
                        String serverMessagesStr = json.get("_server_messages").asText();
                        try {
                            ArrayNode messagesArray = (ArrayNode) new ObjectMapper().readTree(serverMessagesStr);
                            StringBuilder allMessages = new StringBuilder();
                            for (JsonNode msgNodeRaw : messagesArray) {
                                JsonNode msgNode = new ObjectMapper().readTree(msgNodeRaw.asText());
                                System.out.println("Miditra 4");
                                if (msgNode.has("message")) {
                                    allMessages.append(msgNode.get("message").asText()).append("\n");
                                }
                            }
                            message = allMessages.toString().trim();
                        } catch (Exception innerEx) {
                            message = serverMessagesStr; // fallback brut
                        }
                    } else {
                        message = body; // fallback général
                    }

                  throw new Exception(message);


                } catch (Exception e) {
                    throw new Exception(e.getMessage(), e);
                }
            }

            return response.getBody();
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }
}
