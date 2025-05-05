package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.model.SupplierQuotation;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.SupplierQuotationService;
import itu.zazart.erpnext.service.SupplierService;
import itu.zazart.erpnext.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import itu.zazart.erpnext.model.Supplier;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Vector;

@Controller
public class SupplierController {

    private final SessionService sessionService;
    private final SupplierService supplierService;
    private final SupplierQuotationService supplierQuotationService;

    public SupplierController(SessionService sessionService, SupplierService supplierService, SupplierQuotationService supplierQuotationService) {
        this.sessionService = sessionService;
        this.supplierService = supplierService;
        this.supplierQuotationService = supplierQuotationService;
    }

    @GetMapping("/supplier")
    public String supplier(Model model) {
        if (sessionService.isLoggedIn()) {
            return "redirect:/";
        }

        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        Vector<Supplier> suppliers = supplierService.getAllSuppliers(sid);
        model.addAttribute("suppliers", suppliers);

        return "page/supplier";
    }

    @GetMapping("/supplier_quotation")
    public String supplier_quotation(Model model, @RequestParam String name) {
        if (sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        Vector<SupplierQuotation> supplierQuotations = supplierQuotationService.getSupplierQuotationBySupllier(sid,name);
        model.addAttribute("supplierQuotations", supplierQuotations);

        return "page/supplier_quotation";
    }

}

