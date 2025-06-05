package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.dto.DataImport;
import itu.zazart.erpnext.dto.ImportError;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.model.hr.*;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.*;
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
    private final CompanyService companyService;
    private final EmployeeService employeeService;
    private final SalaryComponentService salaryComponentService;
    private final SalaryStructureService salaryStructureService;
    private final SalaryStructureAssignmentService salaryStructureAssignmentService;

    public HRController(SessionService sessionService, ImportService importService, CompanyService companyService, EmployeeService employeeService, SalaryComponentService salaryComponentService, SalaryStructureService salaryStructureService, SalaryStructureAssignmentService salaryStructureAssignmentService) {
        this.sessionService = sessionService;
        this.importService = importService;
        this.companyService = companyService;
        this.employeeService = employeeService;
        this.salaryComponentService = salaryComponentService;
        this.salaryStructureService = salaryStructureService;
        this.salaryStructureAssignmentService = salaryStructureAssignmentService;
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
            List<Company> existingCompanies = companyService.getAllCompanies(sid);
            List<Employee> existingEmployees = employeeService.getAllEmployee(sid);
            List<SalaryComponent> existingSalaryComponents = salaryComponentService.getAllSalaryComponent(sid);
            List<SalaryStructure> existingSalaryStructures = salaryStructureService.getAllSalaryStructure(sid);
            List<SalaryStructureAssignment> existingSalaryStructureAssignments = salaryStructureAssignmentService.getAllSalaryStructureAssignment(sid);

            dataImport.setExistingCompanies(existingCompanies);
            dataImport.setExistingEmployees(existingEmployees);
            dataImport.setExistingSalaryComponents(existingSalaryComponents);
            dataImport.setExistingSalaryStructures(existingSalaryStructures);
            dataImport.setExistingSalaryStructureAssignments(existingSalaryStructureAssignments);
            dataImport.initAbbrList();

            importService.importData(dataImport,importErrorList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error while importing");
            model.addAttribute("importErrorList", importErrorList);
            return "page/hr/import";
        }

        model.addAttribute("successMessage", "Import réussi !");
        return "page/hr/import";
    }
}
