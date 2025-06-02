package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.model.hr.Company;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import itu.zazart.erpnext.service.Utils;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class CompanyService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public CompanyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<Company> getAllCompanies(String sid) {
        String url = erpnextApiUrl + "/api/resource/Company?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                Vector<Company> companies = new Vector<>();
                for (Map<String, Object> item : data) {
                    Company comp = new Company();
                    comp.setName((String) item.get("name"));
                    comp.setCreation(Utils.parseDate(item.get("creation")));
                    comp.setModified(Utils.parseDate(item.get("modified")));
                    comp.setModifiedBy((String) item.get("modified_by"));
                    comp.setOwner((String) item.get("owner"));
                    comp.setDocstatus(Utils.toInt(item.get("docstatus")));
                    comp.setCompanyName((String) item.get("company_name"));
                    comp.setAbbr((String) item.get("abbr"));
                    comp.setDefaultCurrency((String) item.get("default_currency"));
                    comp.setCountry((String) item.get("country"));
                    comp.setIsGroup(Utils.toInt(item.get("is_group")));
                    companies.add(comp);
                    logger.debug("Mapped Company: {}", comp.getName());
                }
                return companies;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Company from ERPNext: {}", e.getMessage(), e);
        }
        return new Vector<>();
    }
}
