package itu.zazart.erpnext.controller.hr;

import itu.zazart.erpnext.dto.EmployeeSearch;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.model.hr.*;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class EmployeeController {
    private final SessionService sessionService;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private final SalarySlipService salarySlipService;

    public EmployeeController(SessionService sessionService,EmployeeService employeeService, SalarySlipService salarySlipService) {
        this.sessionService = sessionService;
        this.employeeService = employeeService;
        this.salarySlipService = salarySlipService;
    }

    @GetMapping("/employee")
    public String employee(@ModelAttribute("employeeSearch") EmployeeSearch employeeSearch, Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        String sid = sessionService.getErpSid();
        List<Employee> employeeList = employeeService.getEmployees(sid,employeeSearch);
        model.addAttribute("employeeList", employeeList);

        return "page/hr/employee";
    }

    @GetMapping("fiche_employee")
    public String getFicheEmployee(Model model, @RequestParam String employeeName){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);
        String sid = sessionService.getErpSid();
        Employee employee = employeeService.getEmployeeByName(sid,employeeName);
        List<SalarySlip> salarySlipList = salarySlipService.getSalarySlipsByEmployee(sid,employeeName);

        model.addAttribute("localeEn", Locale.ENGLISH);
        model.addAttribute("employee",employee);
        model.addAttribute("salarySlipList", salarySlipList);
        return "page/hr/fiche_employee";
    }
}
