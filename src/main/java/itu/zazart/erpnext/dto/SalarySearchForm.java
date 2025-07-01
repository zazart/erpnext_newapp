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
public class SalarySearchForm {
    private String salaryComponentStr;
    private String signe;
    private String amountStr;

    private BigDecimal amount;

    public void parseBigDecimals() {
        try {
            if (amountStr != null) {
                this.amount = new BigDecimal(amountStr);
            }
        } catch (NumberFormatException ignored) {
        }

    }
}