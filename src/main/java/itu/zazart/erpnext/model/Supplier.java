package itu.zazart.erpnext.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private String name;
    private Date creation;
    private Date modified;
    private String modifiedBy;
    private String owner;
    private int docstatus;
    private int idx;
    private String namingSeries;
    private String supplierName;
    private String country;
    private String supplierGroup;
    private String supplierType;
    private int isTransporter;
    private String image = "";
    private String defaultCurrency;
    private String defaultBankAccount;
    private String defaultPriceList;
    private int isInternalSupplier;
    private String representsCompany;
    private String supplierDetails;
    private String website;
    private String language;
    private String taxId;
    private String taxCategory;
    private String taxWithholdingCategory;
    private String supplierPrimaryAddress;
    private String primaryAddress;
    private String supplierPrimaryContact;
    private String mobileNo;
    private String emailId;
    private String paymentTerms;
    private int allowPurchaseInvoiceCreationWithoutPurchaseOrder;
    private int allowPurchaseInvoiceCreationWithoutPurchaseReceipt;
    private int isFrozen;
    private int disabled;
    private int warnRfqs;
    private int warnPos;
    private int preventRfqs;
    private int preventPos;
    private int onHold;
    private String holdType;
    private Date releaseDate;
    private String userTags;
    private String comments;
    private String assign;
    private String likedBy;


    public String getInitial() {
        if (supplierName != null && !supplierName.isEmpty()) {
            return supplierName.substring(0, 1).toUpperCase();
        }
        return "?";
    }
}
