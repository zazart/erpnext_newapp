package itu.zazart.erpnext.service.hr;

import itu.zazart.erpnext.dto.EmployeeSearch;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.model.hr.SalaryComponent;
import itu.zazart.erpnext.model.hr.SalarySlip;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SalarySlipService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalarySlipService.class);


    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalarySlipService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setSalarySlipFields(Map<String, Object> item, SalarySlip salarySlip) {
        salarySlip.setName((String) item.get("name"));
        salarySlip.setOwner((String) item.get("owner"));
        salarySlip.setCreation(Utils.toDateTime(item.get("creation")));
        salarySlip.setModified(Utils.toDateTime(item.get("modified")));
        salarySlip.setModifiedBy((String) item.get("modified_by"));
        salarySlip.setDocstatus(Utils.toInt(item.get("docstatus")));
        salarySlip.setIdx(Utils.toInt(item.get("idx")));
        salarySlip.setEmployee((String) item.get("employee"));
        salarySlip.setEmployee_name((String) item.get("employee_name"));
        salarySlip.setCompany((String) item.get("company"));
        salarySlip.setPosting_date(Utils.toDate(item.get("posting_date")));
        salarySlip.setStatus((String) item.get("status"));
        salarySlip.setCurrency((String) item.get("currency"));
        salarySlip.setExchange_rate(BigDecimal.valueOf((Double) item.get("exchange_rate")));
        salarySlip.setPayroll_frequency((String) item.get("payroll_frequency"));
        salarySlip.setStart_date(Utils.toDate(item.get("start_date")));
        salarySlip.setEnd_date(Utils.toDate(item.get("end_date")));
        salarySlip.setSalary_structure((String) item.get("salary_structure"));
        salarySlip.setPayroll_entry((String) item.get("payroll_entry"));

//        salarySlip.setDepartment((String) item.get("department"));
//        salarySlip.setDesignation((String) item.get("designation"));
//        salarySlip.setBranch((String) item.get("branch"));
//        salarySlip.setLetter_head((String) item.get("letter_head"));
//        salarySlip.setSalary_withholding((String) item.get("salary_withholding"));
//        salarySlip.setSalary_withholding_cycle((String) item.get("salary_withholding_cycle"));
//        salarySlip.setJournal_entry((String) item.get("journal_entry"));
//        salarySlip.setAmended_from((String) item.get("amended_from"));
//        salarySlip.setBank_name((String) item.get("bank_name"));
//        salarySlip.setBank_account_no((String) item.get("bank_account_no"));

        salarySlip.setMode_of_payment((String) item.get("mode_of_payment"));
        salarySlip.setSalary_slip_based_on_timesheet(Utils.toInt(item.get("salary_slip_based_on_timesheet")));
        salarySlip.setDeduct_tax_for_unclaimed_employee_benefits(Utils.toInt(item.get("deduct_tax_for_unclaimed_employee_benefits")));
        salarySlip.setDeduct_tax_for_unsubmitted_tax_exemption_proof(Utils.toInt(item.get("deduct_tax_for_unsubmitted_tax_exemption_proof")));
        salarySlip.setTotal_working_days(BigDecimal.valueOf((Double) item.get("total_working_days")));
        salarySlip.setUnmarked_days(BigDecimal.valueOf((Double) item.get("unmarked_days")));
        salarySlip.setLeave_without_pay(BigDecimal.valueOf((Double) item.get("leave_without_pay")));
        salarySlip.setAbsent_days(BigDecimal.valueOf((Double) item.get("absent_days")));
        salarySlip.setPayment_days(BigDecimal.valueOf((Double) item.get("payment_days")));
        salarySlip.setTotal_working_hours(BigDecimal.valueOf((Double) item.get("total_working_hours")));
        salarySlip.setHour_rate(BigDecimal.valueOf((Double) item.get("hour_rate")));
        salarySlip.setBase_hour_rate(BigDecimal.valueOf((Double) item.get("base_hour_rate")));
        salarySlip.setGross_pay(BigDecimal.valueOf((Double) item.get("gross_pay")));
        salarySlip.setBase_gross_pay(BigDecimal.valueOf((Double) item.get("base_gross_pay")));
        salarySlip.setGross_year_to_date(BigDecimal.valueOf((Double) item.get("gross_year_to_date")));
        salarySlip.setBase_gross_year_to_date(BigDecimal.valueOf((Double) item.get("base_gross_year_to_date")));
        salarySlip.setTotal_deduction(BigDecimal.valueOf((Double) item.get("total_deduction")));
        salarySlip.setBase_total_deduction(BigDecimal.valueOf((Double) item.get("base_total_deduction")));
        salarySlip.setNet_pay(BigDecimal.valueOf((Double) item.get("net_pay")));
        salarySlip.setBase_net_pay(BigDecimal.valueOf((Double) item.get("base_net_pay")));
        salarySlip.setRounded_total(BigDecimal.valueOf((Double) item.get("rounded_total")));
        salarySlip.setBase_rounded_total(BigDecimal.valueOf((Double) item.get("base_rounded_total")));
        salarySlip.setYear_to_date(BigDecimal.valueOf((Double) item.get("year_to_date")));
        salarySlip.setBase_year_to_date(BigDecimal.valueOf((Double) item.get("base_year_to_date")));
        salarySlip.setMonth_to_date(BigDecimal.valueOf((Double) item.get("month_to_date")));
        salarySlip.setBase_month_to_date(BigDecimal.valueOf((Double) item.get("base_month_to_date")));
        salarySlip.setTotal_in_words((String) item.get("total_in_words"));
        salarySlip.setBase_total_in_words((String) item.get("base_total_in_words"));
        salarySlip.setCtc(BigDecimal.valueOf((Double) item.get("ctc")));
        salarySlip.setIncome_from_other_sources(BigDecimal.valueOf((Double) item.get("income_from_other_sources")));
        salarySlip.setTotal_earnings(BigDecimal.valueOf((Double) item.get("total_earnings")));
        salarySlip.setNon_taxable_earnings(BigDecimal.valueOf((Double) item.get("non_taxable_earnings")));
        salarySlip.setStandard_tax_exemption_amount(BigDecimal.valueOf((Double) item.get("standard_tax_exemption_amount")));
        salarySlip.setTax_exemption_declaration(BigDecimal.valueOf((Double) item.get("tax_exemption_declaration")));
        salarySlip.setDeductions_before_tax_calculation(BigDecimal.valueOf((Double) item.get("deductions_before_tax_calculation")));
        salarySlip.setAnnual_taxable_amount(BigDecimal.valueOf((Double) item.get("annual_taxable_amount")));
        salarySlip.setIncome_tax_deducted_till_date(BigDecimal.valueOf((Double) item.get("income_tax_deducted_till_date")));
        salarySlip.setCurrent_month_income_tax(BigDecimal.valueOf((Double) item.get("current_month_income_tax")));
        salarySlip.setFuture_income_tax_deductions(BigDecimal.valueOf((Double) item.get("future_income_tax_deductions")));
        salarySlip.setTotal_income_tax(BigDecimal.valueOf((Double) item.get("total_income_tax")));
    }

    public void setComponents(Map<String, Object> ss, SalarySlip salarySlip) {
        List<Map<String, Object>> earnings = (List<Map<String, Object>>) ss.get("earnings");
        List<SalaryComponent> earns = new ArrayList<>();
        for (Map<String, Object> item : earnings) {
            SalaryComponent earning = new SalaryComponent();
            earning.setSalaryComponent((String) item.get("salary_component"));
            earning.setAmount(BigDecimal.valueOf((Double) item.get("amount")));
            earns.add(earning);
        }

        List<Map<String, Object>> deductions = (List<Map<String, Object>>) ss.get("deductions");
        List<SalaryComponent> deducts = new ArrayList<>();
        for (Map<String, Object> item : deductions) {
            SalaryComponent deduction = new SalaryComponent();
            deduction.setSalaryComponent((String) item.get("salary_component"));
            deduction.setAmount(BigDecimal.valueOf((Double) item.get("amount")));
            deducts.add(deduction);
        }

        salarySlip.setEarnings(earns);
        salarySlip.setDeductions(deducts);
    }

    public List<SalarySlip> getSalarySlipsByEmployee(String sid, String employee) {
        String url = erpnextApiUrl + "/api/resource/Salary Slip?filters=[[\"employee\",\"=\",\""+employee+"\"]]&fields=[\"*\"]";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

                List<SalarySlip> listSalarySlip = new ArrayList<>();
                for (Map<String, Object> item : data) {
                    SalarySlip salarySlip = new SalarySlip();
                    setSalarySlipFields(item, salarySlip);
                    listSalarySlip.add(salarySlip);
                    logger.debug("Mapped SalarySlip: {}", salarySlip.getName());
                }
                return listSalarySlip;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Employee from ERPNext: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public SalarySlip  getSalarySlipByName(String sid,String name){
        String url = erpnextApiUrl + "/api/resource/Salary Slip/"+name;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                SalarySlip salarySlip = new SalarySlip();
                setSalarySlipFields(data, salarySlip);
                setComponents(data, salarySlip);
                return salarySlip;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Slip from ERPNext: {}", e.getMessage(), e);
        }
        return null;
    }
}
