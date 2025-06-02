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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class SalaryStructureService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalaryStructureService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryStructureService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<SalaryStructure> getAllSalaryStructure(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Structure?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                Vector<SalaryStructure> listSalaryStructure = new Vector<>();
                for (Map<String, Object> item : data) {
                    SalaryStructure salaryStructure = new SalaryStructure();
                    salaryStructure.setName((String) item.get("name"));
                    salaryStructure.setCreation(Utils.parseDate(item.get("creation")));
                    salaryStructure.setModified(Utils.parseDate(item.get("modified")));
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
        return new Vector<>();
    }

}
