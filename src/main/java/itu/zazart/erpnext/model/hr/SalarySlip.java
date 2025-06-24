package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalarySlip {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String employee;
    private String employeeName;
    private String company;
    private String department;
    private String designation;
    private String branch;
    private LocalDate postingDate;
    private String letterHead;
    private String status;
    private String salaryWithholding;
    private String salaryWithholdingCycle;
    private String currency;
    private BigDecimal exchangeRate;
    private String payrollFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String salaryStructure;
    private String payrollEntry;
    private String modeOfPayment;

    private int salarySlipBasedOnTimesheet;
    private int deductTaxForUnclaimedEmployeeBenefits;
    private int deductTaxForUnsubmittedTaxExemptionProof;

    private BigDecimal totalWorkingDays;
    private BigDecimal unmarkedDays;
    private BigDecimal leaveWithoutPay;
    private BigDecimal absentDays;
    private BigDecimal paymentDays;
    private BigDecimal totalWorkingHours;
    private BigDecimal hourRate;
    private BigDecimal baseHourRate;
    private BigDecimal grossPay;
    private BigDecimal baseGrossPay;
    private BigDecimal grossYearToDate;
    private BigDecimal baseGrossYearToDate;
    private BigDecimal totalDeduction;
    private BigDecimal baseTotalDeduction;
    private BigDecimal netPay;
    private BigDecimal baseNetPay;
    private BigDecimal roundedTotal;
    private BigDecimal baseRoundedTotal;
    private BigDecimal yearToDate;
    private BigDecimal baseYearToDate;
    private BigDecimal monthToDate;
    private BigDecimal baseMonthToDate;
    private String totalInWords;
    private String baseTotalInWords;
    private BigDecimal ctc;
    private BigDecimal incomeFromOtherSources;
    private BigDecimal totalEarnings;
    private BigDecimal nonTaxableEarnings;
    private BigDecimal standardTaxExemptionAmount;
    private BigDecimal taxExemptionDeclaration;
    private BigDecimal deductionsBeforeTaxCalculation;
    private BigDecimal annualTaxableAmount;
    private BigDecimal incomeTaxDeductedTillDate;
    private BigDecimal currentMonthIncomeTax;
    private BigDecimal futureIncomeTaxDeductions;
    private BigDecimal totalIncomeTax;

    private String journalEntry;
    private String amendedFrom;
    private String bankName;
    private String bankAccountNo;
    private List<SalaryComponent> earnings;
    private List<SalaryComponent> deductions;
    private Employee employeeObject;

    private String salaryStructureAssignment;


}