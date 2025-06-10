package itu.zazart.erpnext.controller.hr;

import itu.zazart.erpnext.dto.RegisterSearch;
import itu.zazart.erpnext.dto.SalaryRegister;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.SalaryRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import itu.zazart.erpnext.service.Utils;

@Controller
public class SalaryRegisterController {
    private final SessionService sessionService;
    private final SalaryRegisterService salaryRegisterService;;
    private static final Logger logger = LoggerFactory.getLogger(SalaryRegisterController.class);

    public SalaryRegisterController(SessionService sessionService, SalaryRegisterService salaryRegisterService) {
        this.sessionService = sessionService;
        this.salaryRegisterService = salaryRegisterService;
    }

    @GetMapping("/register")
    public String register(Model model, @ModelAttribute RegisterSearch registerSearch) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        String sid = sessionService.getErpSid();
        List<SalaryRegister> registerList = salaryRegisterService.getSalaryRegister(sid,registerSearch);
        List<String> columnNames = new ArrayList<>();
        SalaryRegister totalRegister = null;
        if (!registerList.isEmpty()) {
            int i = 0;
            for (String key : registerList.get(0).getExtras().keySet()) {
                columnNames.add(Utils.snakeCaseToWords(key));
            }
            totalRegister =  registerList.get(registerList.size()-1);
        }

        model.addAttribute("columnNames", columnNames);
        model.addAttribute("registerList", registerList);
        model.addAttribute("totalRegister", totalRegister);
        model.addAttribute("user", user);
        return "page/hr/salary_register";
    }
}
