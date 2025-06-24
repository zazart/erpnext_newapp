package itu.zazart.erpnext.controller.hr;

import itu.zazart.erpnext.dto.DataImport;
import itu.zazart.erpnext.dto.ImportError;
import itu.zazart.erpnext.dto.SalaryGenFormat;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.DataService;
import itu.zazart.erpnext.service.hr.EmployeeService;
import itu.zazart.erpnext.service.hr.ImportService;
import itu.zazart.erpnext.service.hr.SalarySlipService;
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
public class DataController {
    private final SessionService sessionService;
    private final DataService dataService;
    private final ImportService importService;
    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    private final SalarySlipService salarySlipService;

    public DataController(SessionService sessionService, DataService dataService, ImportService importService, EmployeeService employeeService, SalarySlipService salarySlipService) {
        this.sessionService = sessionService;
        this.dataService = dataService;
        this.importService = importService;
        this.employeeService = employeeService;
        this.salarySlipService = salarySlipService;
    }

    @GetMapping("/reset")
    public String deleteData(Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        String sid = sessionService.getErpSid();
        dataService.deleteAll(sid);
        return "redirect:/import";
    }

    @GetMapping("/import")
    public String showImportForm(Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);

        model.addAttribute("dataImport", new DataImport());
        return "page/hr/import";
    }

    @PostMapping("/import")
    public String importData(@ModelAttribute("dataImport") DataImport dataImport, Model model) {
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

        model.addAttribute("successMessage", "Import successful !");
        return "page/hr/import";
    }

    @GetMapping("/generate_salary")
    public String showGenerateSalaryForm(Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        String sid = sessionService.getErpSid();
        List<Employee> employeeList = employeeService.getAllEmployee(sid);
        model.addAttribute("user", user);
        model.addAttribute("employeeList", employeeList);
        model.addAttribute("salaryGenFormat", new SalaryGenFormat());
        return "page/hr/generate_salary";
    }

    @PostMapping("/generate_salary")
    public String generateSalary(@ModelAttribute("salaryGenFormat") SalaryGenFormat salaryGenFormat,Model model){
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);
        String sid = sessionService.getErpSid();

        try {
            String employeeName = salaryGenFormat.getEmployeeStr();
            String base =  salaryGenFormat.getBaseStr();
            salaryGenFormat.setEmployee(employeeService.getEmployeeByName(sid,employeeName));
            salaryGenFormat.setBase(base);

            salarySlipService.generateSalarySlips(sid, salaryGenFormat, null);
            model.addAttribute("successMessage", "Data successfully generated !");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while generating Salary !");
            logger.error("Error while generating Salary ", e);
        }

        List<Employee> employeeList = employeeService.getAllEmployee(sid);
        model.addAttribute("employeeList", employeeList);
        return "page/hr/generate_salary";
    }

}
