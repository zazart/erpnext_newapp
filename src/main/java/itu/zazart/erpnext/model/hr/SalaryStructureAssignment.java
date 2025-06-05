package itu.zazart.erpnext.model.hr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryStructureAssignment {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String employee;
    private String employeeName;
    private String department;
    private String designation;
    private String grade;
    private String salaryStructure;
    private LocalDate fromDate;
    private String incomeTaxSlab;
    private String company;
    private String payrollPayableAccount;
    private String currency;
    private BigDecimal base;
    private BigDecimal variable;
    private String amendedFrom;
    private BigDecimal taxableEarningsTillDate;
    private BigDecimal taxDeductedTillDate;
}