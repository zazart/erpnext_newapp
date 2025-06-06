package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.dto.DataImport;
import itu.zazart.erpnext.dto.EmployeeSearch;
import itu.zazart.erpnext.dto.ImportError;
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
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HRController {
    private final SessionService sessionService;
    private final ImportService importService;
    private static final Logger logger = LoggerFactory.getLogger(HRController.class);
    private final EmployeeService employeeService;

    public HRController(SessionService sessionService, ImportService importService, EmployeeService employeeService) {
        this.sessionService = sessionService;
        this.importService = importService;
        this.employeeService = employeeService;
    }

    @GetMapping("/import")
    public String supplier(Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        model.addAttribute("dataImport", new DataImport());
        return "page/hr/import";
    }

    @PostMapping("/import")
    public String handleFileUpload(@ModelAttribute("dataImport") DataImport dataImport, Model model) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);
        List<ImportError> importErrorList = new ArrayList<>();
        try {
            String sid = sessionService.getErpSid();
            importService.importData(dataImport,importErrorList,sid);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while importing");
            model.addAttribute("importErrorList", importErrorList);
            logger.error("Error while importing data", e);
            return "page/hr/import";
        }

        model.addAttribute("successMessage", "Import réussi !");
        return "page/hr/import";
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
}
