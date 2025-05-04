package itu.zazart.erpnext.service;

import itu.zazart.erpnext.model.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class SupplierService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public SupplierService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<Supplier> getAllSuppliers(String sid) {
        String url = erpnextApiUrl + "/api/resource/Supplier?fields=[\"*\"]";
        logger.info("Fetching suppliers from URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            logger.debug("Received response with status: {}", response.getStatusCode());

            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> suppliersData = (List<Map<String, Object>>) response.getBody().get("data");
                logger.info("Number of suppliers fetched: {}", suppliersData.size());

                Vector<Supplier> suppliers = new Vector<>();
                for (Map<String, Object> supplierData : suppliersData) {
                    Supplier supplier = new Supplier();
                    supplier.setName((String) supplierData.get("name"));
                    supplier.setCreation(parseDate(supplierData.get("creation")));
                    supplier.setModified(parseDate(supplierData.get("modified")));
                    supplier.setModifiedBy((String) supplierData.get("modified_by"));
                    supplier.setOwner((String) supplierData.get("owner"));
                    supplier.setDocstatus(toInt(supplierData.get("docstatus")));
                    supplier.setIdx(toInt(supplierData.get("idx")));
                    supplier.setNamingSeries((String) supplierData.get("naming_series"));
                    supplier.setSupplierName((String) supplierData.get("supplier_name"));
                    supplier.setCountry((String) supplierData.get("country"));
                    supplier.setSupplierGroup((String) supplierData.get("supplier_group"));
                    supplier.setSupplierType((String) supplierData.get("supplier_type"));
                    supplier.setIsTransporter(toInt(supplierData.get("is_transporter")));
                    supplier.setImage((String) supplierData.get("image"));
                    supplier.setDefaultCurrency((String) supplierData.get("default_currency"));
                    supplier.setDefaultBankAccount((String) supplierData.get("default_bank_account"));
                    supplier.setDefaultPriceList((String) supplierData.get("default_price_list"));
                    supplier.setIsInternalSupplier(toInt(supplierData.get("is_internal_supplier")));
                    supplier.setRepresentsCompany((String) supplierData.get("represents_company"));
                    supplier.setSupplierDetails((String) supplierData.get("supplier_details"));
                    supplier.setWebsite((String) supplierData.get("website"));
                    supplier.setLanguage((String) supplierData.get("language"));
                    supplier.setTaxId((String) supplierData.get("tax_id"));
                    supplier.setTaxCategory((String) supplierData.get("tax_category"));
                    supplier.setTaxWithholdingCategory((String) supplierData.get("tax_withholding_category"));
                    supplier.setSupplierPrimaryAddress((String) supplierData.get("supplier_primary_address"));
                    supplier.setPrimaryAddress((String) supplierData.get("primary_address"));
                    supplier.setSupplierPrimaryContact((String) supplierData.get("supplier_primary_contact"));
                    supplier.setMobileNo((String) supplierData.get("mobile_no"));
                    supplier.setEmailId((String) supplierData.get("email_id"));
                    supplier.setPaymentTerms((String) supplierData.get("payment_terms"));
                    supplier.setAllowPurchaseInvoiceCreationWithoutPurchaseOrder(toInt(supplierData.get("allow_purchase_invoice_creation_without_purchase_order")));
                    supplier.setAllowPurchaseInvoiceCreationWithoutPurchaseReceipt(toInt(supplierData.get("allow_purchase_invoice_creation_without_purchase_receipt")));
                    supplier.setIsFrozen(toInt(supplierData.get("is_frozen")));
                    supplier.setDisabled(toInt(supplierData.get("disabled")));
                    supplier.setWarnRfqs(toInt(supplierData.get("warn_rfqs")));
                    supplier.setWarnPos(toInt(supplierData.get("warn_pos")));
                    supplier.setPreventRfqs(toInt(supplierData.get("prevent_rfqs")));
                    supplier.setPreventPos(toInt(supplierData.get("prevent_pos")));
                    supplier.setOnHold(toInt(supplierData.get("on_hold")));
                    supplier.setHoldType((String) supplierData.get("hold_type"));
                    supplier.setReleaseDate(parseDate(supplierData.get("release_date")));
                    supplier.setUserTags((String) supplierData.get("user_tags"));
                    supplier.setComments((String) supplierData.get("comments"));
                    supplier.setAssign((String) supplierData.get("assign"));
                    supplier.setLikedBy((String) supplierData.get("liked_by"));

                    logger.debug("Mapped supplier: {}", supplier.getName());
                    suppliers.add(supplier);
                }

                logger.info("Successfully mapped {} suppliers.", suppliers.size());
                return suppliers;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }

        } catch (Exception e) {
            logger.error("Error fetching suppliers from ERPNext: {}", e.getMessage(), e);
        }

        return new Vector<>();
    }

    private Date parseDate(Object date) {
        if (date == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) date);
        } catch (ParseException e) {
            logger.warn("Failed to parse date: {}", date);
            return null;
        }
    }

    private int toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse integer from string: {}", obj);
            }
        }
        return 0;
    }
}
