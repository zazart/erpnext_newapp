package itu.zazart.erpnext.service;

import itu.zazart.erpnext.dto.ItemUpdateRequest;
import itu.zazart.erpnext.model.SupplierQuotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SupplierQuotationService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SupplierQuotationService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SupplierQuotationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<SupplierQuotation> getSupplierQuotationBySupllier(String sid,String name){
        String url = erpnextApiUrl
                + "/api/resource/Supplier Quotation?filters=[[\"supplier\",\"=\",\"" + name + "\"]]"
                + "&fields=[\"supplier_name\",\"status\",\"transaction_date\",\"valid_till\",\"grand_total\",\"name\"]";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        logger.debug("Received response with status: {}", response.getStatusCode());

        if (response.getBody() != null && response.getBody().containsKey("data")) {
            Vector<SupplierQuotation> valiny = new Vector<>();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");
            logger.info("Number of supplier quotation fetched: {}", dataList.size());

            for (Map<String, Object> data : dataList) {
                SupplierQuotation sq = new SupplierQuotation();
                sq.setSupplierName((String) data.get("supplier_name"));
                sq.setName((String) data.get("name"));
                sq.setStatus((String) data.get("status"));
                sq.setGrandTotal(BigDecimal.valueOf((Double) data.get("grand_total")));
                sq.setTransactionDate(parseDate(data.get("transaction_date")));
                sq.setValidTill(parseDate(data.get("valid_till")));
                valiny.add(sq);
            }
            return valiny;
        }
        return new Vector<>();
    }

    private Date parseDate(Object date) {
        if (date == null) return null;
        try {
            String dateStr = (String) date;
            return java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to parse date with valueOf: {}", date);
            return null;
        }
    }


    public Vector<Map<String, Object>> getItemSupplierQuotation(String sid, String name) {
        String url = erpnextApiUrl + "/api/resource/Supplier Quotation/" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Vector<Map<String, Object>> items = new Vector<>();

        if (response.getBody() != null && response.getBody().containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

            List<Map<String, Object>> itemList = (List<Map<String, Object>>) data.get("items");
            for (Map<String, Object> item : itemList) {
                Map<String, Object> itemDetails = new HashMap<>();
                itemDetails.put("name", item.get("name"));
                itemDetails.put("item_code", item.get("item_code"));
                itemDetails.put("item_name", item.get("item_name"));
                itemDetails.put("qty", item.get("qty"));
                itemDetails.put("rate", item.get("rate"));
                itemDetails.put("amount", item.get("amount"));
                itemDetails.put("description", item.get("description"));

                items.add(itemDetails);
            }
        }
        return items;
    }


    public ResponseEntity<String> updateItemsPrices(String sid, String quotationName, List<ItemUpdateRequest> updates) {
        String url = erpnextApiUrl + "/api/resource/Supplier Quotation/" + quotationName;

        logger.info("Updating prices for quotation: {}", quotationName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);

        try {
            HttpEntity<Void> getEntity = new HttpEntity<>(headers);
            var response = restTemplate.exchange(url, HttpMethod.GET, getEntity, Map.class);

            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");

            for (ItemUpdateRequest update : updates) {
                for (Map<String, Object> item : items) {
                    if (update.getName().equals(item.get("name"))) {
                        item.put("rate", update.getNewRate());
                        break;
                    }
                }
            }

            Map<String, Object> updateBody = new HashMap<>();
            updateBody.put("items", items);
            updateBody.put("docstatus", 1);

            HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateBody, headers);
            ResponseEntity<String> updateResponse = restTemplate.exchange(url, HttpMethod.PUT, updateEntity, String.class);

            logger.info("Quotation '{}' updated successfully", quotationName);

            return updateResponse;
        } catch (Exception e) {
            logger.error("Error updating quotation '{}'", quotationName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating quotation");
        }
    }

}
