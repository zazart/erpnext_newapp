package itu.zazart.erpnext.service.buying;

import itu.zazart.erpnext.model.buying.PurchaseOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class PurchaseOrderService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public PurchaseOrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<PurchaseOrder> getPurchaseOrderBySupllier(String sid, String name){
        String url = erpnextApiUrl
                + "/api/resource/Purchase Order?filters=[[\"supplier\",\"=\",\"" + name + "\"]]"
                + "&fields=[\"supplier_name\",\"name\",\"status\",\"total\",\"per_received\",\"per_billed\",\"modified\"]";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("data")) {
            Vector<PurchaseOrder> valiny = new Vector<>();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.getBody().get("data");

            for (Map<String, Object> data : dataList) {
                PurchaseOrder purchaseOrder = new PurchaseOrder();

                purchaseOrder.setSupplierName((String) data.get("supplier_name"));
                purchaseOrder.setName((String) data.get("name"));
                purchaseOrder.setStatus((String) data.get("status"));
                purchaseOrder.setTotal(BigDecimal.valueOf((Double) data.get("total")));
                purchaseOrder.setPerReceived(BigDecimal.valueOf((Double) data.get("per_received")));
                purchaseOrder.setPerBilled(BigDecimal.valueOf((Double) data.get("per_billed")));
                purchaseOrder.setModified(parseDate(data.get("modified")));

                valiny.add(purchaseOrder);
            }
            return valiny;
        }
        return new Vector<>();
    }

    private Date parseDate(Object date) {
        if (date == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) date);
        } catch (ParseException e) {
            logger.warn("Failed to parse date with valueOf: {}", date);
            return null;
        }
    }
}
