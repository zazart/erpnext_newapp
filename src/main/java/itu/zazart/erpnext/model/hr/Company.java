package itu.zazart.erpnext.model.hr;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private String name;
    private String owner;
    private LocalDateTime creation;
    private LocalDateTime modified;
    private String modifiedBy;
    private int docstatus;
    private int idx;

    private String companyName;
    private String abbr;
    private String defaultCurrency;
    private String country;
    private int isGroup;
    private String defaultHolidayList;
    private String defaultLetterHead;
    private String taxId;
    private String domain;
    private LocalDate dateOfEstablishment;
    private String parentCompany;
    private String companyLogo;
    private LocalDate dateOfIncorporation;
    private String phoneNo;
    private String email;
    private String companyDescription;
    private LocalDate dateOfCommencement;
    private String fax;
    private String website;
    private String registrationDetails;

    private int lft;
    private int rgt;
    private String oldParent;
    private String createChartOfAccountsBasedOn;
    private String existingCompany;
    private String chartOfAccounts;
    private String defaultBankAccount;
    private String defaultCashAccount;
    private String defaultReceivableAccount;
    private String defaultPayableAccount;
    private String writeOffAccount;
    private String unrealizedProfitLossAccount;
    private int allowAccountCreationAgainstChildCompany;
    private String defaultExpenseAccount;
    private String defaultIncomeAccount;
    private String defaultDiscountAccount;
    private String paymentTerms;
    private String costCenter;
    private String defaultFinanceBook;
    private String exchangeGainLossAccount;
    private String unrealizedExchangeGainLossAccount;
    private String roundOffAccount;
    private String roundOffCostCenter;
    private String roundOffForOpening;
    private String defaultDeferredRevenueAccount;
    private String defaultDeferredExpenseAccount;
    private int bookAdvancePaymentsInSeparatePartyAccount;
    private int reconcileOnAdvancePaymentLocalDate;
    private String reconciliationTakesEffectOn;
    private String defaultAdvanceReceivedAccount;
    private String defaultAdvancePaidAccount;
    private int autoExchangeRateRevaluation;
    private String autoErrFrequency;
    private int submitErrJv;
    private String exceptionBudgetApproverRole;
    private String accumulatedDepreciationAccount;
    private String depreciationExpenseAccount;
    private String seriesForDepreciationEntry;
    private String disposalAccount;
    private String depreciationCostCenter;
    private String capitalWorkInProgressAccount;
    private String assetReceivedButNotBilled;
    private String defaultBuyingTerms;
    private String salesMonthlyHistory;
    private double monthlySalesTarget;
    private double totalMonthlySales;
    private String defaultSellingTerms;
    private String defaultWarehouseForSalesReturn;
    private double creditLimit;
    private String defaultExpenseClaimPayableAccount;
    private String defaultEmployeeAdvanceAccount;
    private String defaultPayrollPayableAccount;
    private int enablePerpetualInventory;
    private int enableProvisionalAccountingForNonStockItems;
    private String defaultInventoryAccount;
    private String stockAdjustmentAccount;
    private String stockReceivedButNotBilled;
    private String defaultProvisionalAccount;
    private String defaultInTransitWarehouse;
    private String defaultOperatingCostAccount;
}
