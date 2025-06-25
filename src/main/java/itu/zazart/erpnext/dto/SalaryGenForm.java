package itu.zazart.erpnext.dto;

import itu.zazart.erpnext.model.hr.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryGenForm {
    private String baseStr;
    private String startMonthStr;
    private String endMonthStr;

    private LocalDate startMonth;
    private LocalDate endMonth;
    private BigDecimal base;

    private String employeeStr;
    private Employee employee;

    private String ecraser;
    private String isAverage;


    public void setStartMonthStr(String startMonthStr) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        this.startMonth = YearMonth.parse(startMonthStr, monthFormatter).atDay(1);
        this.startMonthStr = startMonthStr;
    }

    public void setEndMonthStr(String endMonthStr) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        this.endMonth = YearMonth.parse(endMonthStr, monthFormatter).atEndOfMonth();
        this.endMonthStr = endMonthStr;
    }

    public void setBase(String base) {
        try {
            this.base = new BigDecimal(base);
        } catch (NumberFormatException e) {
            this.base = BigDecimal.ZERO;
        }
    }
    public void setBase(BigDecimal bigDecimal){
        this.base = bigDecimal;
    }
}
