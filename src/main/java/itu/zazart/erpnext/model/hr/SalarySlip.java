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
public class SalarySlip {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String employee;
    private String employee_name;
    private String company;
    private String department;
    private String designation;
    private String branch;
    private LocalDate posting_date;
    private String letter_head;
    private String status;
    private String salary_withholding;
    private String salary_withholding_cycle;
    private String currency;
    private BigDecimal exchange_rate;
    private String payroll_frequency;
    private LocalDate start_date;
    private LocalDate end_date;
    private String salary_structure;
    private String payroll_entry;
    private String mode_of_payment;

    private int salary_slip_based_on_timesheet;
    private int deduct_tax_for_unclaimed_employee_benefits;
    private int deduct_tax_for_unsubmitted_tax_exemption_proof;

    private BigDecimal total_working_days;
    private BigDecimal unmarked_days;
    private BigDecimal leave_without_pay;
    private BigDecimal absent_days;
    private BigDecimal payment_days;
    private BigDecimal total_working_hours;
    private BigDecimal hour_rate;
    private BigDecimal base_hour_rate;
    private BigDecimal gross_pay;
    private BigDecimal base_gross_pay;
    private BigDecimal gross_year_to_date;
    private BigDecimal base_gross_year_to_date;
    private BigDecimal total_deduction;
    private BigDecimal base_total_deduction;
    private BigDecimal net_pay;
    private BigDecimal base_net_pay;
    private BigDecimal rounded_total;
    private BigDecimal base_rounded_total;
    private BigDecimal year_to_date;
    private BigDecimal base_year_to_date;
    private BigDecimal month_to_date;
    private BigDecimal base_month_to_date;
    private String total_in_words;
    private String base_total_in_words;
    private BigDecimal ctc;
    private BigDecimal income_from_other_sources;
    private BigDecimal total_earnings;
    private BigDecimal non_taxable_earnings;
    private BigDecimal standard_tax_exemption_amount;
    private BigDecimal tax_exemption_declaration;
    private BigDecimal deductions_before_tax_calculation;
    private BigDecimal annual_taxable_amount;
    private BigDecimal income_tax_deducted_till_date;
    private BigDecimal current_month_income_tax;
    private BigDecimal future_income_tax_deductions;
    private BigDecimal total_income_tax;

    private String journal_entry;
    private String amended_from;
    private String bank_name;
    private String bank_account_no;
}
