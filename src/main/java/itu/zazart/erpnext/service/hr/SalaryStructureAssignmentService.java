package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.model.hr.SalaryStructureAssignment;
import itu.zazart.erpnext.service.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SalaryStructureAssignmentService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalaryStructureAssignmentService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryStructureAssignmentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SalaryStructureAssignment> getAllSalaryStructureAssignment(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment?fields=[\"*\"]";

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
                    salaryStructureAssignment.setName((String) item.get("name"));
                    salaryStructureAssignment.setCreation(Utils.toDateTime(item.get("creation")));
                    salaryStructureAssignment.setModified(Utils.toDateTime(item.get("modified")));
                    salaryStructureAssignment.setModifiedBy((String) item.get("modified_by"));
                    salaryStructureAssignment.setOwner((String) item.get("owner"));
                    salaryStructureAssignment.setDocstatus(Utils.toInt(item.get("docstatus")));
                    salaryStructureAssignment.setIdx(Utils.toInt(item.get("idx")));

                    salaryStructureAssignment.setEmployee((String) item.get("employee"));
                    salaryStructureAssignment.setEmployeeName((String) item.get("employee_name"));
                    salaryStructureAssignment.setDepartment((String) item.get("department"));
                    salaryStructureAssignment.setDesignation((String) item.get("designation"));
                    salaryStructureAssignment.setGrade((String) item.get("grade"));
                    salaryStructureAssignment.setSalaryStructure((String) item.get("salary_structure"));
                    salaryStructureAssignment.setFromDate(Utils.toDate(item.get("from_date")));
                    salaryStructureAssignment.setIncomeTaxSlab((String) item.get("income_tax_slab"));
                    salaryStructureAssignment.setCompany((String) item.get("company"));
                    salaryStructureAssignment.setPayrollPayableAccount((String) item.get("payroll_payable_account"));
                    salaryStructureAssignment.setCurrency((String) item.get("currency"));
                    salaryStructureAssignment.setBase(BigDecimal.valueOf(Utils.toInt(item.get("base"))));
                    salaryStructureAssignment.setVariable(BigDecimal.valueOf(Utils.toInt(item.get("variable"))));
                    salaryStructureAssignment.setAmendedFrom((String) item.get("amended_from"));
                    salaryStructureAssignment.setTaxableEarningsTillDate(BigDecimal.valueOf(Utils.toInt(item.get("taxable_earnings_till_date"))));
                    salaryStructureAssignment.setTaxDeductedTillDate(BigDecimal.valueOf(Utils.toInt(item.get("tax_deducted_till_date"))));

                    listSalaryStructureAssignment.add(salaryStructureAssignment);
                    logger.debug("Mapped Salary Structure Assignment: {}", salaryStructureAssignment.getName());
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


    public String newSalaryStructureAssignment(String sid, SalaryStructureAssignment assignment) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure Assignment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("employee", assignment.getEmployee());
            requestBody.put("salary_structure", assignment.getSalaryStructure());
            requestBody.put("from_date", Utils.formatDate(assignment.getFromDate()));
            requestBody.put("company", assignment.getCompany());
            requestBody.put("currency", assignment.getCurrency());
            requestBody.put("docstatus", 1);
            requestBody.put("base", assignment.getBase());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error creating new Salary Structure Assignment", e);
        }
    }


}
