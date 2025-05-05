package itu.zazart.erpnext.service;

import itu.zazart.erpnext.model.SupplierQuotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
        String filtreUrls = erpnextApiUrl
                + "/api/resource/Supplier Quotation?filters=[[\"supplier\",\"=\",\"" + name + "\"]]"
                + "&fields=[\"supplier_name\",\"status\",\"transaction_date\",\"valid_till\",\"grand_total\",\"name\"]";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(filtreUrls, HttpMethod.GET, entity, Map.class);
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


}
