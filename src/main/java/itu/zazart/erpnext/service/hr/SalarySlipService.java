package itu.zazart.erpnext.service.hr;


import itu.zazart.erpnext.dto.SalaryGenForm;
import itu.zazart.erpnext.dto.SalaryUpdateForm;
import itu.zazart.erpnext.model.hr.SalaryComponent;
import itu.zazart.erpnext.model.hr.SalarySlip;
import itu.zazart.erpnext.model.hr.SalaryStructureAssignment;
import itu.zazart.erpnext.service.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SalarySlipService {

    private final RestTemplate restTemplate;
    private final DataService dataService;
    private static final Logger logger = LoggerFactory.getLogger(SalarySlipService.class);
    private final SalaryStructureAssignmentService salaryStructureAssignmentService;

    @Autowired
    DataSource dataSource;

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalarySlipService(RestTemplate restTemplate, DataService dataService, SalaryStructureAssignmentService salaryStructureAssignmentService) {
        this.restTemplate = restTemplate;
        this.dataService = dataService;
        this.salaryStructureAssignmentService = salaryStructureAssignmentService;
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
        salarySlip.setEmployeeName((String) item.get("employee_name"));
        salarySlip.setCompany((String) item.get("company"));
        salarySlip.setPostingDate(Utils.toDate(item.get("posting_date")));
        salarySlip.setStatus((String) item.get("status"));
        salarySlip.setCurrency((String) item.get("currency"));
        salarySlip.setExchangeRate(BigDecimal.valueOf((Double) item.get("exchange_rate")));
        salarySlip.setPayrollFrequency((String) item.get("payroll_frequency"));
        salarySlip.setStartDate(Utils.toDate(item.get("start_date")));
        salarySlip.setEndDate(Utils.toDate(item.get("end_date")));
        salarySlip.setSalaryStructure((String) item.get("salary_structure"));
        salarySlip.setPayrollEntry((String) item.get("payroll_entry"));

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

        salarySlip.setModeOfPayment((String) item.get("mode_of_payment"));
        salarySlip.setSalarySlipBasedOnTimesheet(Utils.toInt(item.get("salary_slip_based_on_timesheet")));
        salarySlip.setDeductTaxForUnclaimedEmployeeBenefits(Utils.toInt(item.get("deduct_tax_for_unclaimed_employee_benefits")));
        salarySlip.setDeductTaxForUnsubmittedTaxExemptionProof(Utils.toInt(item.get("deduct_tax_for_unsubmitted_tax_exemption_proof")));
        salarySlip.setTotalWorkingDays(BigDecimal.valueOf((Double) item.get("total_working_days")));
        salarySlip.setUnmarkedDays(BigDecimal.valueOf((Double) item.get("unmarked_days")));
        salarySlip.setLeaveWithoutPay(BigDecimal.valueOf((Double) item.get("leave_without_pay")));
        salarySlip.setAbsentDays(BigDecimal.valueOf((Double) item.get("absent_days")));
        salarySlip.setPaymentDays(BigDecimal.valueOf((Double) item.get("payment_days")));
        salarySlip.setTotalWorkingHours(BigDecimal.valueOf((Double) item.get("total_working_hours")));
        salarySlip.setHourRate(BigDecimal.valueOf((Double) item.get("hour_rate")));
        salarySlip.setBaseHourRate(BigDecimal.valueOf((Double) item.get("base_hour_rate")));
        salarySlip.setGrossPay(BigDecimal.valueOf((Double) item.get("gross_pay")));
        salarySlip.setBaseGrossPay(BigDecimal.valueOf((Double) item.get("base_gross_pay")));
        salarySlip.setGrossYearToDate(BigDecimal.valueOf((Double) item.get("gross_year_to_date")));
        salarySlip.setBaseGrossYearToDate(BigDecimal.valueOf((Double) item.get("base_gross_year_to_date")));
        salarySlip.setTotalDeduction(BigDecimal.valueOf((Double) item.get("total_deduction")));
        salarySlip.setBaseTotalDeduction(BigDecimal.valueOf((Double) item.get("base_total_deduction")));
        salarySlip.setNetPay(BigDecimal.valueOf((Double) item.get("net_pay")));
        salarySlip.setBaseNetPay(BigDecimal.valueOf((Double) item.get("base_net_pay")));
        salarySlip.setRoundedTotal(BigDecimal.valueOf((Double) item.get("rounded_total")));
        salarySlip.setBaseRoundedTotal(BigDecimal.valueOf((Double) item.get("base_rounded_total")));
        salarySlip.setYearToDate(BigDecimal.valueOf((Double) item.get("year_to_date")));
        salarySlip.setBaseYearToDate(BigDecimal.valueOf((Double) item.get("base_year_to_date")));
        salarySlip.setMonthToDate(BigDecimal.valueOf((Double) item.get("month_to_date")));
        salarySlip.setMonthToDate(BigDecimal.valueOf((Double) item.get("base_month_to_date")));
        salarySlip.setTotalInWords((String) item.get("total_in_words"));
        salarySlip.setBaseTotalInWords((String) item.get("base_total_in_words"));
        salarySlip.setCtc(BigDecimal.valueOf((Double) item.get("ctc")));
        salarySlip.setIncomeFromOtherSources(BigDecimal.valueOf((Double) item.get("income_from_other_sources")));
        salarySlip.setTotalEarnings(BigDecimal.valueOf((Double) item.get("total_earnings")));
        salarySlip.setNonTaxableEarnings(BigDecimal.valueOf((Double) item.get("non_taxable_earnings")));
        salarySlip.setStandardTaxExemptionAmount(BigDecimal.valueOf((Double) item.get("standard_tax_exemption_amount")));
        salarySlip.setTaxExemptionDeclaration(BigDecimal.valueOf((Double) item.get("tax_exemption_declaration")));
        salarySlip.setDeductionsBeforeTaxCalculation(BigDecimal.valueOf((Double) item.get("deductions_before_tax_calculation")));
        salarySlip.setAnnualTaxableAmount(BigDecimal.valueOf((Double) item.get("annual_taxable_amount")));
        salarySlip.setIncomeTaxDeductedTillDate(BigDecimal.valueOf((Double) item.get("income_tax_deducted_till_date")));
        salarySlip.setCurrentMonthIncomeTax(BigDecimal.valueOf((Double) item.get("current_month_income_tax")));
        salarySlip.setFutureIncomeTaxDeductions(BigDecimal.valueOf((Double) item.get("future_income_tax_deductions")));
        salarySlip.setTotalIncomeTax(BigDecimal.valueOf((Double) item.get("total_income_tax")));
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
        String url = erpnextApiUrl + "/api/resource/Salary Slip"
                + "?limit_page_length=1000"
                + "&filters=[[\"employee\",\"=\",\"" + employee + "\"], [\"docstatus\", \"=\", 1]]"
                + "&fields=[\"*\"]";
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
                    logger.info("Mapped SalarySlip: {}", salarySlip.getName());
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

    public void getSalarySlipByName(String sid,SalarySlip salarySlip){
        String url = erpnextApiUrl + "/api/resource/Salary Slip/"+salarySlip.getName();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                setSalarySlipFields(data, salarySlip);
                setComponents(data, salarySlip);
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e) {
            logger.error("Error fetching Salary Slip from ERPNext: {}", e.getMessage(), e);
        }
    }


    public String newSalarySlip(String sid, SalarySlip salarySlip) {
        String url = erpnextApiUrl + "/api/resource/Salary Slip";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        try {
            logger.info("Insertion of the salary slip employee:{}", salarySlip.getEmployee());
            Map<String, Object> requestBody = new HashMap<>();
            // Format des dates correct : "yyyy-MM-dd"
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate start = salarySlip.getStartDate();
            LocalDate end = Utils.getLastDateOfMonth(start.getYear(), start.getMonthValue());

            requestBody.put("employee", salarySlip.getEmployee());
            requestBody.put("company", salarySlip.getCompany());
            requestBody.put("start_date", start.format(formatter));
            requestBody.put("end_date", end.format(formatter));
            requestBody.put("docstatus", 1);
            requestBody.put("salary_structure", salarySlip.getSalaryStructure());
            if ( salarySlip.getSalaryStructureAssignment() != null ){
                requestBody.put("salary_structure_assignment", salarySlip.getSalaryStructureAssignment());
            }
            requestBody.put("payroll_frequency", "Monthly");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error creating new Salary Slip");
            throw new RuntimeException("Error creating new Salary Slip", e);
        }
    }

    public void generateSalarySlips(String sid, SalaryGenForm salaryGenForm, boolean whithNewBase) throws Exception {
        LocalDate current = salaryGenForm.getStartMonth();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String targetDate = salaryGenForm.getStartMonth().format(formatter);
        SalaryStructureAssignment ssa = salaryStructureAssignmentService.getClosestSalaryAssignementId(sid, salaryGenForm.getEmployeeStr(),targetDate);
        if (ssa == null) {
            throw new Exception("Salary Structure Assignment not found");
        }

        SalaryStructureAssignment forAll = null;
        while (!current.isAfter(salaryGenForm.getEndMonth())) {
            YearMonth ym = YearMonth.from(current);
            LocalDate startDate = ym.atDay(1);
            LocalDate endDate = ym.atEndOfMonth();

            SalaryStructureAssignment closestSSA = salaryStructureAssignmentService.getClosestSalaryAssignementId(sid, salaryGenForm.getEmployeeStr(),startDate.format(formatter));
            if (current.isEqual(closestSSA.getFromDate())){
                current = current.plusMonths(1);
                continue;
            }
            if (whithNewBase) {
                closestSSA.setFromDate(startDate);
                closestSSA.setBase(salaryGenForm.getBase());
                salaryStructureAssignmentService.newSalaryStructureAssignment(sid,closestSSA);
            }
            try {
                SalarySlip ss = new SalarySlip();
                ss.setEmployee(salaryGenForm.getEmployeeStr());
                ss.setCompany(salaryGenForm.getEmployee().getCompany());
                ss.setStartDate(startDate);
                ss.setEndDate(endDate);
                ss.setSalaryStructure(closestSSA.getSalaryStructure());
                if (whithNewBase) {
                    ss.setSalaryStructureAssignment(closestSSA.getName());
                }
                newSalarySlip(sid, ss);
            }
            catch (Exception e) {
                if (e.getMessage().contains("already exists") || e.getMessage().contains("Duplicate")) {
                    logger.warn("Salary Slip already exists.");
                } else {
                    logger.warn("Error fetching Salary Slip from ERPNext: {}", e.getMessage(), e);
                }
            }
            current = current.plusMonths(1);
        }
    }


    public void fetchInfoFullOnSalarySlipList(String sid, List<SalarySlip> salarySlipList) throws Exception {
        for (SalarySlip ss : salarySlipList) {
            String employeeName = ss.getEmployee();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String targetDate = ss.getStartDate().format(formatter);
            SalaryStructureAssignment ssaClosest = salaryStructureAssignmentService.getClosestSalaryAssignementId(sid,employeeName,targetDate);
            ss.setSalaryStructureAssignmentObject(ssaClosest);
            getSalarySlipByName(sid,ss);
        }
    }

    public List<SalarySlip> getSalarySlips(String sid) {
        String url = erpnextApiUrl + "/api/resource/Salary Slip"
                + "?limit_page_length=1000"
                + "&filters=[[\"docstatus\", \"=\", 1]]"
                + "&fields=[\"*\"]";

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
                    logger.info("Mapped SalarySlip: {}", salarySlip.getName());
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

    public List<SalarySlip> getFullSalarySlips(String sid) throws Exception {
        List<SalarySlip> salarySlipList = getSalarySlips(sid);
        fetchInfoFullOnSalarySlipList(sid,salarySlipList);
        return salarySlipList;
    }

    public List<SalarySlip> getSalaryFiltered(String sid, SalaryUpdateForm salaryUpdateForm, List<SalarySlip> salarySlipList) throws Exception {
        List<SalarySlip> salarySlipListFiltered = new ArrayList<>();
        String componentName = salaryUpdateForm.getSalaryComponentStr();
        BigDecimal componentMin = salaryUpdateForm.getComponentMin();
        BigDecimal componentMax = salaryUpdateForm.getComponentMax();
        BigDecimal salaryMin = salaryUpdateForm.getSalaryMin();
        BigDecimal salaryMax = salaryUpdateForm.getSalaryMax();
        for(SalarySlip  item : salarySlipList) {
            boolean isValidComponent = false;

            for(SalaryComponent earning : item.getEarnings()) {
                if (earning.getSalaryComponent().equals(componentName)
                    && earning.getAmount().compareTo(componentMin) >= 0
                    && earning.getAmount().compareTo(componentMax) <= 0) {
                    isValidComponent = true;
                }
            }

            for(SalaryComponent deduction : item.getDeductions()) {
                if (deduction.getSalaryComponent().equals(componentName)
                    && deduction.getAmount().compareTo(componentMin) >= 0
                    && deduction.getAmount().compareTo(componentMax) <= 0) {
                    isValidComponent = true;
                }
            }

            if(isValidComponent) {
                SalaryStructureAssignment ssaClosest = item.getSalaryStructureAssignmentObject();
                if (ssaClosest == null) {
                    continue;
                }
                else if ( ssaClosest.getBase().compareTo(salaryMin) >= 0
                            && ssaClosest.getBase().compareTo(salaryMax) <= 0) {
                    salarySlipListFiltered.add(item);
                }
            }
        }
        return salarySlipListFiltered;
    }

    public void cancelSalarySlip (String sid, SalarySlip salarySlip) {
        dataService.cancelDocument(sid, "Salary Slip", salarySlip.getName());
    }

    public void updateSalary(String sid,SalaryUpdateForm salaryUpdateForm) throws Exception {
        salaryUpdateForm.parseBigDecimals();
        List<SalarySlip> salarySlips = getFullSalarySlips(sid);
        List<SalarySlip> salarySlipListFiltered = getSalaryFiltered(sid,salaryUpdateForm,salarySlips);

        for (SalarySlip salarySlip : salarySlipListFiltered) {
            SalaryStructureAssignment ssa = salarySlip.getSalaryStructureAssignmentObject();

            cancelSalarySlip(sid, salarySlip);
            salaryStructureAssignmentService.cancelSalaryStructureAssignment(sid, ssa);

            BigDecimal base = ssa.getBase();
            BigDecimal pct = salaryUpdateForm.getPercentage();
            BigDecimal newBase = base.add(base.multiply(pct).divide(BigDecimal.valueOf(100)));

            ssa.setBase(newBase);
            salaryStructureAssignmentService.newSalaryStructureAssignment(sid, ssa);
            salarySlip.setSalaryStructureAssignment(ssa.getName());
            newSalarySlip(sid, salarySlip);
        }
    }


    public List<SalarySlip> getAllSalarySlipBySQL(Connection connection) throws SQLException {
        List<SalarySlip> salarySlips = new ArrayList<>();
        boolean check = false;
        try {
            if (connection == null) {
                connection = dataSource.getConnection();
                check = true;
            }
            String sql = "SELECT * FROM `tabSalary Slip`";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                SalarySlip salarySlip = new SalarySlip();
                salarySlip.setName(resultSet.getString("name"));
                salarySlip.setEmployee(resultSet.getString("employee"));
                salarySlip.setEmployeeName(resultSet.getString("employee_name"));

                salarySlips.add(salarySlip);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (check) {
                connection.close();
            }
        }
        return salarySlips;
    }


}
