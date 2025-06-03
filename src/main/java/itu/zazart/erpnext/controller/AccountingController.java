package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.model.buying.PurchaseInvoice;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.service.buying.AccountingService;
import itu.zazart.erpnext.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Vector;

@Controller
public class AccountingController {

    private final SessionService sessionService;
    private final AccountingService accountingService;


    public AccountingController(SessionService sessionService, AccountingService accountingService) {
        this.sessionService = sessionService;
        this.accountingService = accountingService;
    }

    @GetMapping("/accounting")
    public String supplier(Model model) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }

        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        Vector<PurchaseInvoice> purchaseInvoices = accountingService.getAllPurchaseInvoices(sid);
        model.addAttribute("purchaseInvoices", purchaseInvoices);

        return "page/accounting";
    }

    @PostMapping("/newPaymentEntry")
    public ResponseEntity<String> paymentEntry(@RequestBody Map<String, Object> data) {
        try {
            String sid = sessionService.getErpSid();
            String response = accountingService.newPayementEntry(data, sid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending payment");
        }
    }
}
