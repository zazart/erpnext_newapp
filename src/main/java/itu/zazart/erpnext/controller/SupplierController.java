package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import itu.zazart.erpnext.model.Supplier;

import java.util.Vector;

@Controller
public class SupplierController {

    private final SessionService sessionService;
    private final SupplierService supplierService;

    public SupplierController(SessionService sessionService, SupplierService supplierService) {
        this.sessionService = sessionService;
        this.supplierService = supplierService;
    }

    @GetMapping("/supplier")
    public String home(Model model) {
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
}

