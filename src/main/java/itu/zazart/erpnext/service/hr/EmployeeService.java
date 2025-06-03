package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.model.hr.Company;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.service.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<Employee> getAllEmployee(String sid) {
        String url = erpnextApiUrl + "/api/resource/Employee?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                Vector<Employee> listEmployee = new Vector<>();
                for (Map<String, Object> item : data) {
                    Employee employee = new Employee();
                    employee.setName((String) item.get("name"));
                    employee.setCreation(Utils.parseDate(item.get("creation")));
                    employee.setModified(Utils.parseDate(item.get("modified")));
                    employee.setModifiedBy((String) item.get("modified_by"));
                    employee.setOwner((String) item.get("owner"));
                    employee.setDocstatus(Utils.toInt(item.get("docstatus")));
                    employee.setEmployee((String) item.get("employee"));
                    employee.setFirstName((String) item.get("first_name"));
                    employee.setMiddleName((String) item.get("middle_name"));
                    employee.setLastName((String) item.get("last_name"));
                    employee.setEmployeeName((String) item.get("employee_name"));
                    employee.setGender((String) item.get("gender"));
                    employee.setDateOfBirth(Utils.parseDate(item.get("date_of_birth")));
                    employee.setStatus((String) item.get("status"));
                    employee.setCompany((String) item.get("company"));
                    listEmployee.add(employee);
                    logger.debug("Mapped Employee: {}", employee.getName());
                }
                return listEmployee;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Employee from ERPNext: {}", e.getMessage(), e);
        }
        return new Vector<>();
    }
}
