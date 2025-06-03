package itu.zazart.erpnext.model.buying;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierQuotation {

    private String name;
    private Date creation;
    private Date modified;
    private String modifiedBy;
    private String owner;
    private int docstatus;
    private int idx;
    private String title;
    private String namingSeries;
    private String supplier;
    private String supplierName;
    private String company;
    private String status;
    private Date transactionDate;
    private Date validTill;
    private String quotationNumber;
    private String amendedFrom;
    private String costCenter;
    private String project;
    private String currency;
    private BigDecimal conversionRate;
    private String buyingPriceList;
    private String priceListCurrency;
    private BigDecimal plcConversionRate;
    private boolean ignorePricingRule;
    private BigDecimal totalQty;
    private BigDecimal totalNetWeight;
    private BigDecimal baseTotal;
    private BigDecimal baseNetTotal;
    private BigDecimal total;
    private BigDecimal netTotal;
    private String taxCategory;
    private String taxesAndCharges;
    private String shippingRule;
    private String incoterm;
    private String namedPlace;
    private BigDecimal baseTaxesAndChargesAdded;
    private BigDecimal baseTaxesAndChargesDeducted;
    private BigDecimal baseTotalTaxesAndCharges;
    private BigDecimal taxesAndChargesAdded;
    private BigDecimal taxesAndChargesDeducted;
    private BigDecimal totalTaxesAndCharges;
    private String applyDiscountOn;
    private BigDecimal baseDiscountAmount;
    private BigDecimal additionalDiscountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal baseGrandTotal;
    private BigDecimal baseRoundingAdjustment;
    private BigDecimal baseRoundedTotal;
    private String baseInWords;
    private BigDecimal grandTotal;
    private BigDecimal roundingAdjustment;
    private BigDecimal roundedTotal;
    private String inWords;
    private boolean disableRoundedTotal;
    private String otherChargesCalculation;
    private String supplierAddress;
    private String addressDisplay;
    private String contactPerson;
    private String contactDisplay;
    private String contactMobile;
    private String contactEmail;
    private String shippingAddress;
    private String shippingAddressDisplay;
    private String billingAddress;
    private String billingAddressDisplay;
    private String tcName;
    private String terms;
    private String letterHead;
    private boolean groupSameItems;
    private String selectPrintHeading;
    private String language;
    private String autoRepeat;
    private boolean isSubcontracted;
    private String opportunity;
    private String userTags;
    private String comments;
    private String assign;
    private String likedBy;
}
