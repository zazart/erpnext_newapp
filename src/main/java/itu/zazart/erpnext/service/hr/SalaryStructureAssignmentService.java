package itu.zazart.erpnext.service.hr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.model.hr.SalaryStructureAssignment;
import itu.zazart.erpnext.service.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SalaryStructureAssignmentService {
    private final RestTemplate restTemplate;
    private final DataService dataService;
    private static final Logger logger = LoggerFactory.getLogger(SalaryStructureAssignmentService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryStructureAssignmentService(RestTemplate restTemplate, DataService dataService) {
        this.restTemplate = restTemplate;
        this.dataService = dataService;
    }

    public void setSalaryStructureAssignmentFields (Map<String, Object> ssa, SalaryStructureAssignment salaryStructureAssignment) {
        salaryStructureAssignment.setName((String) ssa.get("name"));
        salaryStructureAssignment.setCreation(Utils.toDateTime(ssa.get("creation")));
        salaryStructureAssignment.setModified(Utils.toDateTime(ssa.get("modified")));
        salaryStructureAssignment.setModifiedBy((String) ssa.get("modified_by"));
        salaryStructureAssignment.setOwner((String) ssa.get("owner"));
        salaryStructureAssignment.setDocstatus(Utils.toInt(ssa.get("docstatus")));
        salaryStructureAssignment.setIdx(Utils.toInt(ssa.get("idx")));

        salaryStructureAssignment.setEmployee((String) ssa.get("employee"));
        salaryStructureAssignment.setEmployeeName((String) ssa.get("employee_name"));
        salaryStructureAssignment.setDepartment((String) ssa.get("department"));
        salaryStructureAssignment.setDesignation((String) ssa.get("designation"));
        salaryStructureAssignment.setGrade((String) ssa.get("grade"));
        salaryStructureAssignment.setSalaryStructure((String) ssa.get("salary_structure"));
        salaryStructureAssignment.setFromDate(Utils.toDate(ssa.get("from_date")));
        salaryStructureAssignment.setIncomeTaxSlab((String) ssa.get("income_tax_slab"));
        salaryStructureAssignment.setCompany((String) ssa.get("company"));
        salaryStructureAssignment.setPayrollPayableAccount((String) ssa.get("payroll_payable_account"));
        salaryStructureAssignment.setCurrency((String) ssa.get("currency"));
        salaryStructureAssignment.setBase(BigDecimal.valueOf(Utils.toInt(ssa.get("base"))));
        salaryStructureAssignment.setVariable(BigDecimal.valueOf(Utils.toInt(ssa.get("variable"))));
        salaryStructureAssignment.setAmendedFrom((String) ssa.get("amended_from"));
        salaryStructureAssignment.setTaxableEarningsTillDate(BigDecimal.valueOf(Utils.toInt(ssa.get("taxable_earnings_till_date"))));
        salaryStructureAssignment.setTaxDeductedTillDate(BigDecimal.valueOf(Utils.toInt(ssa.get("tax_deducted_till_date"))));

    }

    public List<SalaryStructureAssignment> getAllSalaryStructureAssignment(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment"
                + "?limit_page_length=1000"
                + "&fields=[\"*\"]"
                + "&filters=[[\"docstatus\", \"=\", 1]]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<SalaryStructureAssignment> listSalaryStructureAssignment = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    SalaryStructureAssignment salaryStructureAssignment = new SalaryStructureAssignment();
                    setSalaryStructureAssignmentFields(item, salaryStructureAssignment);
                    listSalaryStructureAssignment.add(salaryStructureAssignment);
                    logger.info("Mapped Salary Structure Assignment: {}", salaryStructureAssignment.getName());
                }
                return listSalaryStructureAssignment;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Structure Assignment from ERPNext: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    public void newSalaryStructureAssignment(String sid, SalaryStructureAssignment assignment) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        try {
            logger.info("Insertion of the salary structure assignement employee:{}", assignment.getEmployee());
            logger.info("date:{}", Utils.formatDate(assignment.getFromDate()));
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("employee", assignment.getEmployee());
            requestBody.put("salary_structure", assignment.getSalaryStructure());
            requestBody.put("from_date", Utils.formatDate(assignment.getFromDate()));
            requestBody.put("company", assignment.getCompany());
            requestBody.put("currency", assignment.getCurrency());
            requestBody.put("docstatus", 1);
            requestBody.put("base", assignment.getBase());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            var response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                setSalaryStructureAssignmentFields(data, assignment);
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error creating new Salary Structure Assignment");
//            throw new RuntimeException("Error creating new Salary Structure Assignment", e);
        }
    }

    public SalaryStructureAssignment getClosestSalaryAssignementId(String sid, String employeName, String targetDate) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String filters = String.format("[[\"employee\",\"=\", \"%s\"], [\"from_date\", \"<=\", \"%s\"], [\"docstatus\", \"=\", 1]]", employeName, targetDate);
        String url = UriComponentsBuilder.fromHttpUrl(erpnextApiUrl + "/api/resource/Salary Structure Assignment")
                .queryParam("fields", "[\"*\"]")
                .queryParam("filters", filters)
                .queryParam("limite_page_length", "1000")
                .queryParam("order_by","from_date desc")
                .build(false)
                .toUriString();

        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<SalaryStructureAssignment> listSalaryStructureAssignment = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    SalaryStructureAssignment salaryStructureAssignment = new SalaryStructureAssignment();
                    setSalaryStructureAssignmentFields(item, salaryStructureAssignment);

                    logger.info("Mapped Salary Structure Assignment: {}", salaryStructureAssignment.getName());
                    return salaryStructureAssignment;
                }
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Structure Assignment from ERPNext: {}", e.getMessage(), e);
        }
        return null;
    }

    public void cancelSalaryStructureAssignment(String sid, SalaryStructureAssignment ssa) {
        dataService.cancelDocument(sid, "Salary Structure Assignment", ssa.getName());
    }
}
