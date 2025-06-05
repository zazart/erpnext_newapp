package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryComponent {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private int isActive;
    private String salaryComponent;
    private String salaryComponentAbbr;
    private String type;
    private String description;
    private int dependsOnPaymentDays;
    private int isTaxApplicable;
    private int deductFullTaxOnSelectedPayrollDate;
    private int variableBasedOnTaxableSalary;
    private int isIncomeTaxComponent;
    private int exemptedFromIncomeTax;
    private int roundToTheNearestInteger;
    private int statisticalComponent;
    private int doNotIncludeInTotal;
    private int removeIfZeroValued;
    private int disabled;
    private String condition;
    private BigDecimal amount;
    private int amountBasedOnFormula;
    private String formula;
    private int isFlexibleBenefit;
    private BigDecimal maxBenefitAmount;
    private int payAgainstBenefitClaim;
    private int onlyTaxImpact;
}
