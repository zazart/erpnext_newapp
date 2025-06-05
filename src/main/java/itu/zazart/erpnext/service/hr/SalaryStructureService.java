package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.model.hr.SalaryComponent;
import itu.zazart.erpnext.model.hr.SalaryStructure;
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
public class SalaryStructureService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalaryStructureService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryStructureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SalaryStructure> getAllSalaryStructure(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<SalaryStructure> listSalaryStructure = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    SalaryStructure salaryStructure = new SalaryStructure();
                    salaryStructure.setName((String) item.get("name"));
                    salaryStructure.setCreation(Utils.toDateTime(item.get("creation")));
                    salaryStructure.setModified(Utils.toDateTime(item.get("modified")));
                    salaryStructure.setModifiedBy((String) item.get("modified_by"));
                    salaryStructure.setOwner((String) item.get("owner"));
                    salaryStructure.setDocstatus(Utils.toInt(item.get("docstatus")));
                    salaryStructure.setIdx(Utils.toInt(item.get("idx")));

                    salaryStructure.setLetterHead((String) item.get("letter_head"));
                    salaryStructure.setIsActive((String)item.get("is_active"));
                    salaryStructure.setIsDefault((String)item.get("is_default"));
                    salaryStructure.setCurrency((String)item.get("currency"));
                    salaryStructure.setAmendedFrom((String)item.get("amended_from"));
                    salaryStructure.setLeaveEncashmentAmountPerDay(BigDecimal.valueOf((Double) item.get("leave_encashment_amount_per_day")));
                    salaryStructure.setMaxBenefits(BigDecimal.valueOf((Double) item.get("max_benefits")));
                    salaryStructure.setSalarySlipBasedOnTimesheet(Utils.toInt(item.get("salary_slip_based_on_timesheet")));
                    salaryStructure.setPayrollFrequency((String)item.get("payroll_frequency"));
                    salaryStructure.setSalaryComponent((String)item.get("salary_component"));
                    salaryStructure.setHourRate(BigDecimal.valueOf((Double) item.get("hour_rate")));
                    salaryStructure.setTotalEarning(BigDecimal.valueOf((Double) item.get("total_earning")));
                    salaryStructure.setTotalDeduction(BigDecimal.valueOf((Double) item.get("total_deduction")));
                    salaryStructure.setNetPay(BigDecimal.valueOf((Double) item.get("net_pay")));
                    salaryStructure.setModeOfPayment((String)item.get("mode_of_payment"));
                    salaryStructure.setPaymentAccount((String)item.get("payment_account"));
                    listSalaryStructure.add(salaryStructure);
                    logger.debug("Mapped Salary Structure: {}", salaryStructure.getName());
                }
                return listSalaryStructure;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Structure from ERPNext: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }
    public String newSalaryStructure(String sid, SalaryStructure salaryStructure) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", salaryStructure.getName());
            requestBody.put("company", salaryStructure.getCompany());
            requestBody.put("docstatus", 1);
            requestBody.put("currency", salaryStructure.getCurrency());
            requestBody.put("is_active", "Yes");

            // Convert earnings
            List<Map<String, Object>> earningsList = new ArrayList<>();
            for (SalaryComponent earning : salaryStructure.getEarnings()) {
                Map<String, Object> earningMap = new HashMap<>();
                earningMap.put("salary_component", earning.getSalaryComponent());
                earningMap.put("amount", earning.getAmount());
                earningsList.add(earningMap);
            }
            requestBody.put("earnings", earningsList);

            // Convert deductions
            List<Map<String, Object>> deductionsList = new ArrayList<>();
            for (SalaryComponent deduction : salaryStructure.getDeductions()) {
                Map<String, Object> deductionMap = new HashMap<>();
                deductionMap.put("salary_component", deduction.getSalaryComponent());
                deductionMap.put("amount", deduction.getAmount());
                deductionsList.add(deductionMap);
            }
            requestBody.put("deductions", deductionsList);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error creating new Salary Structure", e);
        }
    }


}
