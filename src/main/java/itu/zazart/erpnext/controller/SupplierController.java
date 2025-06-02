package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.dto.ItemUpdateRequest;
import itu.zazart.erpnext.model.buying.PurchaseOrder;
import itu.zazart.erpnext.model.buying.SupplierQuotation;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.service.*;
import itu.zazart.erpnext.service.buying.PurchaseOrderService;
import itu.zazart.erpnext.service.buying.SupplierQuotationService;
import itu.zazart.erpnext.service.buying.SupplierService;
import itu.zazart.erpnext.service.hr.EmployeeService;
import itu.zazart.erpnext.service.hr.SalaryComponentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import itu.zazart.erpnext.model.buying.Supplier;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Vector;

@Controller
public class SupplierController {

    private final SessionService sessionService;
    private final SupplierService supplierService;
    private final SupplierQuotationService supplierQuotationService;
    private final PurchaseOrderService purchaseOrderService;
    private final SalaryComponentService employeeService;

    public SupplierController(SessionService sessionService, SupplierService supplierService, SupplierQuotationService supplierQuotationService, PurchaseOrderService purchaseOrderService, SalaryComponentService employeeService) {
        this.sessionService = sessionService;
        this.supplierService = supplierService;
        this.supplierQuotationService = supplierQuotationService;
        this.purchaseOrderService = purchaseOrderService;
        this.employeeService = employeeService;
    }

    @GetMapping("/supplier")
    public String supplier(Model model) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }

        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        Vector<Supplier> suppliers = supplierService.getAllSuppliers(sid);
        employeeService.getAllSalaryComponent(sid);
        model.addAttribute("suppliers", suppliers);


        return "page/supplier";
    }

    @GetMapping("/supplier_quotation")
    public String supplierQuotation(Model model, @RequestParam String name) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        Vector<SupplierQuotation> supplierQuotations = supplierQuotationService.getSupplierQuotationBySupllier(sid,name);
        model.addAttribute("supplierQuotations", supplierQuotations);

        Supplier supplier = supplierService.getSupplierByName(name,sid);
        model.addAttribute("supplier", supplier);

        Vector<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrderBySupllier(sid,name);
        model.addAttribute("purchaseOrders", purchaseOrders);

        return "page/supplier_quotation";
    }

    @GetMapping("/itemsBySupplierQuotation")
    public ResponseEntity<Vector<Map<String, Object>>> getItemsBySupplierQuotation(@RequestParam String name) {
        try {
            String sid = sessionService.getErpSid();
            Vector<Map<String, Object>> quota = supplierQuotationService.getItemSupplierQuotation(sid, name);
            return ResponseEntity.ok(quota);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateItemsPrices")
    public ResponseEntity<String> updateItemsPrices(@RequestParam String quotationName, @RequestBody List<ItemUpdateRequest> updates) {
        try {
            String sid = sessionService.getErpSid();
            return supplierQuotationService.updateItemsPrices(sid, quotationName, updates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

