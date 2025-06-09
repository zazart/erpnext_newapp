package itu.zazart.erpnext.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryRegister {
    private String salarySlipId;
    private String employee;
    private String employeeName;
    private String dateOfJoining;
    private String branch;
    private String department;
    private String designation;
    private String company;
    private String startDate;
    private String endDate;
    private BigDecimal leaveWithoutPay;
    private BigDecimal absentDays;
    private BigDecimal paymentDays;
    private BigDecimal grossPay;
    private BigDecimal totalLoanRepayment;
    private BigDecimal totalDeduction;
    private BigDecimal netPay;
    private String currency;
    private Map<String, Object> extras;


}
