package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.model.hr.SalaryComponent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryComponentService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalaryComponentService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryComponentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<SalaryComponent> getAllSalaryComponent(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Component?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<SalaryComponent> listSalaryComponent = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    SalaryComponent salaryComponent = new SalaryComponent();
                    salaryComponent.setName((String) item.get("name"));
                    salaryComponent.setCreation(Utils.toDateTime(item.get("creation")));
                    salaryComponent.setModified(Utils.toDateTime(item.get("modified")));
                    salaryComponent.setModifiedBy((String) item.get("modified_by"));
                    salaryComponent.setOwner((String) item.get("owner"));
                    salaryComponent.setDocstatus(Utils.toInt(item.get("docstatus")));
                    salaryComponent.setIdx(Utils.toInt(item.get("idx")));

                    salaryComponent.setSalaryComponent((String) item.get("salary_component"));
                    salaryComponent.setSalaryComponentAbbr((String) item.get("salary_component_abbr"));
                    salaryComponent.setType((String) item.get("type"));
                    salaryComponent.setDescription((String) item.get("description"));

                    salaryComponent.setDependsOnPaymentDays(Utils.toInt(item.get("depends_on_payment_days")));
                    salaryComponent.setIsIncomeTaxComponent(Utils.toInt(item.get("is_income_tax_component")));
                    salaryComponent.setDeductFullTaxOnSelectedPayrollDate(Utils.toInt(item.get("deduct_full_tax_on_selected_payroll_date")));
                    salaryComponent.setVariableBasedOnTaxableSalary(Utils.toInt(item.get("variable_based_on_taxable_salary")));
                    salaryComponent.setIsIncomeTaxComponent(Utils.toInt(item.get("is_income_tax_component")));
                    salaryComponent.setExemptedFromIncomeTax(Utils.toInt(item.get("exempted_from_income_tax_component")));
                    salaryComponent.setRoundToTheNearestInteger(Utils.toInt(item.get("round_to_the_nearest_integer")));
                    salaryComponent.setStatisticalComponent(Utils.toInt(item.get("statistical_component")));
                    salaryComponent.setDoNotIncludeInTotal(Utils.toInt(item.get("do_not_include_in_total")));
                    salaryComponent.setRemoveIfZeroValued(Utils.toInt(item.get("remove_if_zero_valued")));
                    salaryComponent.setDisabled(Utils.toInt(item.get("disabled")));
                    salaryComponent.setCondition((String) item.get("condition"));
                    salaryComponent.setAmount(BigDecimal.valueOf((Double) item.get("amount")));
                    salaryComponent.setAmountBasedOnFormula(Utils.toInt(item.get("amount_based_on_formula")));
                    salaryComponent.setFormula((String) item.get("formula"));
                    salaryComponent.setIsFlexibleBenefit(Utils.toInt(item.get("is_flexible_benefit")));
                    salaryComponent.setMaxBenefitAmount(BigDecimal.valueOf((Double) item.get("max_benefit_amount")));
                    salaryComponent.setPayAgainstBenefitClaim(Utils.toInt(item.get("pay_against_benefit_claim")));
                    salaryComponent.setOnlyTaxImpact(Utils.toInt(item.get("only_tax_impact")));

                    listSalaryComponent.add(salaryComponent);
                    logger.debug("Mapped Salary Component: {}", salaryComponent.getName());
                }
                return listSalaryComponent;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Component from ERPNext: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    public String newSalaryComponent(String sid, SalaryComponent salaryComponent) {
        String url = erpnextApiUrl + "/api/resource/Salary Component";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("salary_component", salaryComponent.getSalaryComponent());
            requestBody.put("salary_component_abbr", salaryComponent.getSalaryComponentAbbr());
            requestBody.put("type", salaryComponent.getType());
            requestBody.put("is_active", 1);
            requestBody.put("amount_based_on_formula", 1);
            requestBody.put("depends_on_payment_days", 0);
            requestBody.put("formula", salaryComponent.getFormula());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("Error creating new Salary Component", e);
        }
    }


}
