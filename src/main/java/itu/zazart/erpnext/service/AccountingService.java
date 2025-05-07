package itu.zazart.erpnext.service;

import itu.zazart.erpnext.model.PurchaseInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;

@Service
public class AccountingService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    @Value("${erpnext.api.url}")
    private String erpnextApiUrl;

    public AccountingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Vector<PurchaseInvoice> getAllPurchaseInvoices(String sid){
        String url = erpnextApiUrl + "/api/resource/Purchase Invoice?fields=[\"*\"]";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "sid=" + sid);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("data")) {
                List<Map<String, Object>> facturesData = (List<Map<String, Object>>) response.getBody().get("data");

                Vector<PurchaseInvoice> purchaceInvoices = new Vector<>();
                for (Map<String, Object> data : facturesData) {
                    PurchaseInvoice pi = new PurchaseInvoice();
                    pi.setName((String) data.get("name"));
                    pi.setStatus((String) data.get("status"));
                    pi.setCreation(parseDate(data.get("creation")));
                    pi.setModified(parseDate(data.get("modified")));
                    pi.setModifiedBy((String) data.get("modified_by"));
                    pi.setOwner((String) data.get("owner"));
                    pi.setDocstatus(toInt(data.get("docstatus")));
                    pi.setIdx(toInt(data.get("idx")));
                    pi.setTitle((String) data.get("title"));
                    pi.setNamingSeries((String) data.get("naming_series"));
                    pi.setSupplier((String) data.get("supplier"));
                    pi.setSupplierName((String) data.get("supplier_name"));
                    pi.setTaxId((String) data.get("tax_id"));
                    pi.setCompany((String) data.get("company"));
                    pi.setPostingDate(parseDate(data.get("posting_date")));
                    pi.setPostingTime(parseTime(data.get("posting_time")));
                    pi.setSetPostingTime(toBoolean(data.get("set_posting_time")));
                    pi.setDueDate(parseDate(data.get("due_date")));
                    pi.setPaid(toBoolean(data.get("is_paid")));
                    pi.setReturn(toBoolean(data.get("is_return")));
                    pi.setReturnAgainst((String) data.get("return_against"));
                    pi.setUpdateOutstandingForSelf(toBoolean(data.get("update_outstanding_for_self")));
                    pi.setUpdateBilledAmountInPurchaseOrder(toBoolean(data.get("update_billed_amount_in_purchase_order")));
                    pi.setUpdateBilledAmountInPurchaseReceipt(toBoolean(data.get("update_billed_amount_in_purchase_receipt")));
                    pi.setApplyTds(toBoolean(data.get("apply_tds")));
                    pi.setTaxWithholdingCategory((String) data.get("tax_withholding_category"));
                    pi.setAmendedFrom((String) data.get("amended_from"));
                    pi.setBillNo((String) data.get("bill_no"));
                    pi.setBillDate(parseDate(data.get("bill_date")));
                    pi.setCostCenter((String) data.get("cost_center"));
                    pi.setProject((String) data.get("project"));
                    pi.setCurrency((String) data.get("currency"));
                    pi.setConversionRate(BigDecimal.valueOf((Double) data.get("conversion_rate")));
                    pi.setUseTransactionDateExchangeRate(toBoolean(data.get("use_transaction_date_exchange_rate")));
                    pi.setBuyingPriceList((String) data.get("buying_price_list"));
                    pi.setPriceListCurrency((String) data.get("price_list_currency"));
                    pi.setPlcConversionRate(BigDecimal.valueOf((Double) data.get("plc_conversion_rate")));
                    pi.setIgnorePricingRule(toBoolean(data.get("ignore_pricing_rule")));
                    pi.setScanBarcode((String) data.get("scan_barcode"));
                    pi.setUpdateStock(toBoolean(data.get("update_stock")));
                    pi.setSetWarehouse((String) data.get("set_warehouse"));
                    pi.setSetFromWarehouse((String) data.get("set_from_warehouse"));
                    pi.setSubcontracted(toBoolean(data.get("is_subcontracted")));
                    pi.setRejectedWarehouse((String) data.get("rejected_warehouse"));
                    pi.setSupplierWarehouse((String) data.get("supplier_warehouse"));
                    pi.setTotalQty(BigDecimal.valueOf((Double) data.get("total_qty")));
                    pi.setTotalNetWeight(BigDecimal.valueOf((Double) data.get("total_net_weight")));
                    pi.setGrandTotal(BigDecimal.valueOf((Double) data.get("grand_total")));
                    pi.setPaidAmount(BigDecimal.valueOf((Double) data.get("paid_amount")));
                    pi.setOutstandingAmount(BigDecimal.valueOf((Double) data.get("outstanding_amount")));
                    purchaceInvoices.add(pi);

                    logger.debug("Mapped Purchase Invoice: {}", pi.getName());
                }
                return purchaceInvoices;
            } else {
                logger.warn("No 'data' field found in the response body.");
            }
        } catch (Exception e){
            logger.error("Error fetching Purchase Invoice from ERPNext: {}", e.getMessage(), e);
        }
        return new Vector<>();
    }


    public String newPayementEntry(Map<String, Object> data, String sid) {
        try {
            String referenceName = (String) data.get("name");
            String party = (String) data.get("party");
            double amount = Double.parseDouble(data.get("amount").toString());
            String paidFrom = (String) data.get("paidFrom");
            String paidTo = (String) data.get("paidTo");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("docstatus", 1);
            requestBody.put("doctype", "Payment Entry");
            requestBody.put("payment_type", "Pay");
            requestBody.put("party_type", "Supplier");
            requestBody.put("party", party);
            requestBody.put("paid_from", paidFrom);
            requestBody.put("paid_to", paidTo);
            requestBody.put("paid_amount", amount);
            requestBody.put("received_amount", amount);

            Map<String, Object> reference = new HashMap<>();
            reference.put("reference_doctype", "Purchase Invoice");
            reference.put("reference_name", referenceName);
            reference.put("allocated_amount", amount);

            requestBody.put("references", List.of(reference));
            String baseUrl = erpnextApiUrl + "/api/resource/Payment Entry";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", "sid=" + sid);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error sending payment");
        }
    }



    private Date parseDate(Object date) {
        if (date == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) date);
        } catch (ParseException e) {
            return null;
        }
    }

    private int toInt(Object obj) {
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) return Integer.parseInt((String) obj);
        return 0;
    }

    private LocalTime parseTime(Object obj) {
        if (obj == null) return null;
        try {
            return LocalTime.parse(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private boolean toBoolean(Object obj) {
        return obj != null && Boolean.parseBoolean(obj.toString());
    }



}
