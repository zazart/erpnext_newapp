package itu.zazart.erpnext.service.hr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.zazart.erpnext.dto.RegisterSearch;
import itu.zazart.erpnext.dto.SalaryRegister;
import itu.zazart.erpnext.service.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SalaryRegisterService {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SalaryRegisterService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SalaryRegisterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getText(JsonNode node, String key) {
        return node.has(key) && !node.get(key).isNull() ? node.get(key).asText() : null;
    }

    private BigDecimal getDecimal(JsonNode node, String key) {
        return node.has(key) && node.get(key).isNumber() ? node.get(key).decimalValue() : null;
    }

    Map<String, Object> copyExtrasWithNullValues(Map<String, Object> originalExtras) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (String key : originalExtras.keySet()) {
            copy.put(key, null);
        }
        return copy;
    }


    public SalaryRegister parseTotalSalaryRegister(JsonNode columnsNode, JsonNode totalArrayNode, SalaryRegister registerModel) {
        List<String> standardFields = Arrays.asList(
                "salary_slip_id", "employee", "employee_name", "data_of_joining", "branch", "department",
                "designation", "company", "start_date", "end_date", "leave_without_pay", "absent_days",
                "payment_days", "gross_pay","total_loan_repayment", "total_deduction", "net_pay", "currency"
        );
        SalaryRegister totalRegister = new SalaryRegister();
        Map<String, Object> extras = copyExtrasWithNullValues(registerModel.getExtras());
        for (int i = 0; i < columnsNode.size(); i++) {
            String key = columnsNode.get(i).path("fieldname").asText();
            JsonNode valueNode = totalArrayNode.get(i);
            if (valueNode == null || valueNode.isNull()) continue;
            Object value = valueNode.isNumber() ? valueNode.decimalValue() : valueNode.asText();
            if (standardFields.contains(key) && !(value instanceof String)) {
                switch (key) {
                    case "leave_without_pay": totalRegister.setLeaveWithoutPay((BigDecimal)value); break;
                    case "absent_days": totalRegister.setAbsentDays((BigDecimal) value); break;
                    case "payment_days": totalRegister.setPaymentDays((BigDecimal)value); break;
                    case "gross_pay": totalRegister.setGrossPay((BigDecimal) value); break;
                    case "total_loan_repayment" : totalRegister.setTotalLoanRepayment((BigDecimal)value); break;
                    case "total_deduction": totalRegister.setTotalDeduction((BigDecimal)value); break;
                    case "net_pay": totalRegister.setNetPay((BigDecimal)value); break;
                }
            } else {
                if (extras.containsKey(key)) {
                    extras.put(key,(BigDecimal) value);
                }
            }
        }
        totalRegister.setExtras(extras);
        return totalRegister;
    }

    public List<SalaryRegister> parseSalaryRegisters(String jsonResponse) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        JsonNode results = root.path("message").path("result");
        JsonNode columns = root.path("message").path("columns");

        List<SalaryRegister> registers = new ArrayList<>();

        for (JsonNode item : results) {
            if (item.isObject()) {
                SalaryRegister register = new SalaryRegister();
                register.setSalarySlipId(getText(item, "salary_slip_id"));
                register.setEmployee(getText(item, "employee"));
                register.setEmployeeName(getText(item, "employee_name"));
                register.setDateOfJoining(getText(item, "data_of_joining"));
                register.setBranch(getText(item, "branch"));
                register.setDepartment(getText(item, "department"));
                register.setDesignation(getText(item, "designation"));
                register.setCompany(getText(item, "company"));
                register.setStartDate(getText(item, "start_date"));
                register.setEndDate(getText(item, "end_date"));
                register.setLeaveWithoutPay(getDecimal(item, "leave_without_pay"));
                register.setAbsentDays(getDecimal(item, "absent_days"));
                register.setPaymentDays(getDecimal(item, "payment_days"));
                register.setGrossPay(getDecimal(item,"gross_pay"));
                register.setTotalLoanRepayment(getDecimal(item, "total_loan_repayment"));
                register.setTotalDeduction(getDecimal(item,"total_deduction"));
                register.setNetPay(getDecimal(item,"net_pay"));
                register.setCurrency(getText(item, "currency"));

                Map<String, Object> extras = new LinkedHashMap<>();

                Iterator<Map.Entry<String, JsonNode>> fields = item.fields();
                Set<String> standardFields = Set.of(
                        "salary_slip_id", "employee", "employee_name", "data_of_joining", "branch", "department",
                        "designation", "company", "start_date", "end_date", "leave_without_pay", "absent_days",
                        "payment_days", "gross_pay", "total_loan_repayment","total_deduction", "net_pay", "currency"
                );

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    if (!standardFields.contains(field.getKey())) {
                        JsonNode value = field.getValue();
                        if (value.isNumber()) {
                            extras.put(field.getKey(), value.decimalValue());
                        } else {
                            extras.put(field.getKey(), value.asText(null));
                        }
                    }
                }
                register.setExtras(extras);
                registers.add(register);
            }
        }

        JsonNode lastNode = results.get(results.size() - 1);
        if (lastNode != null && lastNode.isArray() && !registers.isEmpty()) {
            SalaryRegister totalRegister = parseTotalSalaryRegister(columns, lastNode, registers.get(0));
            registers.add(totalRegister);
        }

        return registers;
    }

    public List<SalaryRegister> getSalaryRegister(String sid, RegisterSearch search) {
        try {
            String url = erpnextApiUrl + "/api/method/frappe.desk.query_report.run";

            Map<String, Object> body = new HashMap<>();
            body.put("report_name", "Salary Register");

            Map<String, String> filters = new HashMap<>();
            LocalDate now = LocalDate.now();

            LocalDate defaultStartDate = now.minusYears(300); // 300 ans en arrière
            LocalDate defaultEndDate = now.plusYears(300);    // 300 ans dans le futur

            LocalDate startDate = search.getStartDate() != null ? search.getStartDate() : defaultStartDate;
            LocalDate endDate = search.getEndDate() != null ? search.getEndDate() : defaultEndDate;

            filters.put("from_date", startDate.toString());
            filters.put("to_date", endDate.toString());


            filters.put("company", "");
            filters.put("docstatus", "Submitted");
            body.put("filters", filters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Cookie", "sid=" + sid);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return parseSalaryRegisters(response.getBody());
        } catch (Exception e) {
            logger.error("Error calling the Salary Register : {}", e.getMessage(), e);
        }
        return null;
    }

    public List<SalaryRegister> getSalaryRegisterByYear(String sid,Integer year,int monthNumber) {
        RegisterSearch search = new RegisterSearch();
        LocalDate startDate = LocalDate.of(year, monthNumber, 1);
        search.setStartDate(startDate);
        search.setEndDate(Utils.getLastDateOfMonth(year, monthNumber));

        List<SalaryRegister> list = getSalaryRegister(sid, search);
        if (list.isEmpty()) {
            return null;
        }
        list.get(0).setSearch(search);
        return list;
    }

    public Map<String, Object> getAllSalaryRegisterByYearGroupByMonth(String sid,Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        Map<String, Object> salaryRegisters = new LinkedHashMap<>();
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        int monthNumber = 1;
        for(String month : months) {
            salaryRegisters.put(month, getSalaryRegisterByYear(sid,year,monthNumber));
            monthNumber++;
        }

        return salaryRegisters;
    }


    public Map<String, Object> getDataChart(Map<String,Object> registerLists){
        Set<String> allKeys = new HashSet<>();

        for (Map.Entry<String, Object> entry : registerLists.entrySet()) {
            List<SalaryRegister> valueList = (List<SalaryRegister>) entry.getValue();
            if (valueList != null) {
                if (!valueList.isEmpty()) {
                    SalaryRegister salaryRegister = valueList.get(0);
                    allKeys.addAll(salaryRegister.getExtras().keySet());
                }
            }

        }

        Map<String, Object> data = new HashMap<>();
        for (String key : allKeys) {
            data.put(key, new double[12]);
        }

        data.put("TOTAL",new double[12]);

        int numberOfMonth = 1;
        for (Map.Entry<String, Object> entry : registerLists.entrySet()) {
            List<SalaryRegister> valueList = (List<SalaryRegister>) entry.getValue();
            if (valueList != null) {
                if (!valueList.isEmpty()) {
                    SalaryRegister total = valueList.get(valueList.size() - 1);
                    Map<String, Object> extras = total.getExtras();

                    double[] totalsEntry = (double[]) data.get("TOTAL");
                    totalsEntry[numberOfMonth - 1] = total.getNetPay().doubleValue();

                    for (Map.Entry<String, Object> extraEntry : extras.entrySet()) {
                        String extraKey = extraEntry.getKey();
                        Object extraValue = extraEntry.getValue();

                        if (data.containsKey(extraKey) && extraValue != null) {
                            double[] values = (double[]) data.get(extraKey);
                            values[numberOfMonth - 1] = ((Number) extraValue).doubleValue();
                        }
                    }
                }
            }
            numberOfMonth++;
        }

        return data;
    }
}
