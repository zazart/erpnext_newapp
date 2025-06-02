package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private double leaveEncashmentAmountPerDay;
    private double maxBenefits;
    private int salarySlipBasedOnTimesheet; // 0 or 1
    private String payrollFrequency;
    private String salaryComponent;
    private double hourRate;
    private double totalEarning;
    private double totalDeduction;
    private double netPay;
    private String modeOfPayment;
    private String paymentAccount;
    private String earnings;
    private String deductions;
}