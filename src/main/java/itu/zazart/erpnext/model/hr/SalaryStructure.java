package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryStructure {
    private String name;
    private String owner;
    private Date creation;
    private Date modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String company;
    private String letterHead;
    private String isActive; // "Yes" / "No"
    private String isDefault; // "Yes" / "No"
    private String currency;
    private String amendedFrom;
    private BigDecimal leaveEncashmentAmountPerDay;
    private BigDecimal maxBenefits;
    private int salarySlipBasedOnTimesheet; // 0 or 1
    private String payrollFrequency;
    private String salaryComponent;
    private BigDecimal hourRate;
    private BigDecimal totalEarning;
    private BigDecimal totalDeduction;
    private BigDecimal netPay;
    private String modeOfPayment;
    private String paymentAccount;
    private String earnings;
    private String deductions;
}