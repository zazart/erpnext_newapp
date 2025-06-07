package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.dto.EmployeeSearch;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.service.Utils;
import jakarta.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setEmployeeFields(Map<String, Object> data, Employee employee) {
        employee.setName((String) data.get("name"));
        employee.setCreation(Utils.toDateTime(data.get("creation")));
        employee.setModified(Utils.toDateTime(data.get("modified")));
        employee.setModifiedBy((String) data.get("modified_by"));
        employee.setOwner((String) data.get("owner"));
        employee.setDocstatus(Utils.toInt(data.get("docstatus")));
        employee.setEmployee((String) data.get("employee"));
        employee.setFirstName((String) data.get("first_name"));
        employee.setMiddleName((String) data.get("middle_name"));
        employee.setLastName((String) data.get("last_name"));
        employee.setEmployeeName((String) data.get("employee_name"));
        employee.setDateOfJoining(Utils.toDate(data.get("date_of_joining")));
        employee.setGender((String) data.get("gender"));
        employee.setDateOfBirth(Utils.toDate(data.get("date_of_birth")));
        employee.setStatus((String) data.get("status"));
        employee.setCompany((String) data.get("company"));
    }

    public List<Employee> getAllEmployee(String sid) {
        return getEmployees(sid,null);
    }

    public Employee getEmployeeByName(String sid,String employeeName) {
        EmployeeSearch employeeSearch = new EmployeeSearch();
        employeeSearch.setName(employeeName);
        List<Employee> employeeList = getEmployees(sid, employeeSearch);

        if (employeeList.isEmpty()) {
            return null;
        }
        return employeeList.get(0);
    }

    public List<Employee> getEmployees(String sid, EmployeeSearch employeeSearch) {
        String filters = buildFilters(employeeSearch);
        String url = erpnextApiUrl + "/api/resource/Employee?filters=["+filters+"]&fields=[\"*\"]";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<Employee> listEmployee = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    Employee employee = new Employee();
                    setEmployeeFields(item, employee);
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
        return new ArrayList<>();
    }

    public void newEmployee(String sid, Employee employee) {
        String url = erpnextApiUrl + "/api/resource/Employee";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        try {

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("first_name", employee.getFirstName());
            requestBody.put("last_name", employee.getLastName());
            requestBody.put("company", employee.getCompany());
            requestBody.put("status", "Active");
            requestBody.put("date_of_birth", Utils.formatDate(employee.getDateOfBirth()));
            requestBody.put("date_of_joining",Utils.formatDate(employee.getDateOfJoining()));
            requestBody.put("gender", employee.getGender());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            var response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                setEmployeeFields(data, employee);
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating new Employee", e);
        }
    }


    public String buildFilters(EmployeeSearch search) {
        List<String> filters = new ArrayList<>();

        if (search.getEmployeeName() != null && !search.getEmployeeName().isBlank()) {
            filters.add("[\"employee_name\", \"like\", \"%" + search.getEmployeeName() + "%\"]");
        }
        if (search.getName() != null && !search.getName().isBlank()) {
            filters.add("[\"name\", \"like\", \"%" + search.getName() + "%\"]");
        }
        if (search.getFirstName() != null && !search.getFirstName().isBlank()) {
            filters.add("[\"first_name\", \"like\", \"%" + search.getFirstName() + "%\"]");
        }
        if (search.getLastName() != null && !search.getLastName().isBlank()) {
            filters.add("[\"last_name\", \"like\", \"%" + search.getLastName() + "%\"]");
        }
        if (search.getMiddleName() != null && !search.getMiddleName().isBlank()) {
            filters.add("[\"middle_name\", \"like\", \"%" + search.getMiddleName() + "%\"]");
        }
        if (search.getDepartment() != null && !search.getDepartment().isBlank()) {
            filters.add("[\"department\", \"=\", \"" + search.getDepartment() + "\"]");
        }
        if (search.getDesignation() != null && !search.getDesignation().isBlank()) {
            filters.add("[\"designation\", \"=\", \"" + search.getDesignation() + "\"]");
        }
        if (search.getGender() != null && !search.getGender().isBlank()) {
            filters.add("[\"gender\", \"=\", \"" + search.getGender() + "\"]");
        }
        if (search.getStatus() != null && !search.getStatus().isBlank()) {
            filters.add("[\"status\", \"=\", \"" + search.getStatus() + "\"]");
        }
        if (search.getDateOfBirthMin() != null) {
            filters.add("[\"date_of_birth\", \">=\", \"" + search.getDateOfBirthMin() + "\"]");
        }
        if (search.getDateOfBirthMax() != null) {
            filters.add("[\"date_of_birth\", \"<=\", \"" + search.getDateOfBirthMax() + "\"]");
        }
        if (search.getDateOfJoiningMin() != null) {
            filters.add("[\"date_of_joining\", \">=\", \"" + search.getDateOfJoiningMin() + "\"]");
        }
        if (search.getDateOfJoiningMax() != null) {
            filters.add("[\"date_of_joining\", \"<=\", \"" + search.getDateOfJoiningMax() + "\"]");
        }

        return String.join(",", filters);
    }



}

