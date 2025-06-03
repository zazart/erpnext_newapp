package itu.zazart.erpnext.model.buying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseInvoice {
    private String name;
    private Date creation;
    private Date modified;
    private String modifiedBy;
    private String owner;
    private int docstatus;
    private int idx;
    private String title;
    private String namingSeries;
    private String status;
    private String supplier;
    private String supplierName;
    private String taxId;
    private String company;
    private Date postingDate;
    private LocalTime postingTime;
    private boolean setPostingTime;
    private Date dueDate;
    private boolean isPaid;
    private boolean isReturn;
    private String returnAgainst;
    private boolean updateOutstandingForSelf;
    private boolean updateBilledAmountInPurchaseOrder;
    private boolean updateBilledAmountInPurchaseReceipt;
    private boolean applyTds;
    private String taxWithholdingCategory;
    private String amendedFrom;
    private String billNo;
    private Date billDate;
    private String costCenter;
    private String project;
    private String currency;
    private BigDecimal conversionRate;
    private boolean useTransactionDateExchangeRate;
    private String buyingPriceList;
    private String priceListCurrency;
    private BigDecimal plcConversionRate;
    private boolean ignorePricingRule;
    private String scanBarcode;
    private boolean updateStock;
    private String setWarehouse;
    private String setFromWarehouse;
    private boolean isSubcontracted;
    private String rejectedWarehouse;
    private String supplierWarehouse;
    private BigDecimal totalQty;
    private BigDecimal totalNetWeight;
    private BigDecimal grandTotal;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
}
