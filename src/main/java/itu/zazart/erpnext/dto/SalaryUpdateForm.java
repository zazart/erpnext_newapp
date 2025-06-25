package itu.zazart.erpnext.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalaryUpdateForm {
    private String salaryComponentStr;
    private String componentMinStr;
    private String componentMaxStr;
    private String salaryMinStr;
    private String salaryMaxStr;
    private String percentageStr;

    private BigDecimal componentMin = new BigDecimal("-1E1000");
    private BigDecimal componentMax = new BigDecimal("1E1000");
    private BigDecimal salaryMin = new BigDecimal("-1E1000");
    private BigDecimal salaryMax = new BigDecimal("1E1000");

    private BigDecimal percentage = BigDecimal.ZERO;


    public void parseBigDecimals() {
        try {
            if (componentMinStr != null) {
                this.componentMin = new BigDecimal(componentMinStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (componentMaxStr != null) {
                this.componentMax = new BigDecimal(componentMaxStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (salaryMinStr != null) {
                this.salaryMin = new BigDecimal(salaryMinStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (salaryMaxStr != null) {
                this.salaryMax = new BigDecimal(salaryMaxStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (percentageStr != null) {
                this.percentage = new BigDecimal(percentageStr);
            }
        } catch (NumberFormatException ignored) {}
    }

}
