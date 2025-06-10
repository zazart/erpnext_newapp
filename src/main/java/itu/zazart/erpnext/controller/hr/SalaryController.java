package itu.zazart.erpnext.controller.hr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.model.hr.SalarySlip;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.EmployeeService;
import itu.zazart.erpnext.service.hr.ExportPdfService;
import itu.zazart.erpnext.service.hr.SalaryRegisterService;
import itu.zazart.erpnext.service.hr.SalarySlipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@Controller
public class SalaryController {
    private final SessionService sessionService;
    private final ExportPdfService exportPdfService;
    private final SalarySlipService salarySlipService;
    private final EmployeeService employeeService;
    private final SalaryRegisterService salaryRegisterService;
    private static final Logger logger = LoggerFactory.getLogger(SalaryController.class);

    public SalaryController(SessionService sessionService, ExportPdfService exportPdfService, SalarySlipService salarySlipService, EmployeeService employeeService, SalaryRegisterService salaryRegisterService, SalaryRegisterService salaryRegisterService1) {
        this.sessionService = sessionService;
        this.exportPdfService = exportPdfService;
        this.salarySlipService = salarySlipService;
        this.employeeService = employeeService;
        this.salaryRegisterService = salaryRegisterService1;
    }

    @GetMapping("/print_salary_slip")
    public ResponseEntity<byte[]> printSalarySlip(@RequestParam String salarySlipName) throws Exception {
        String sid = sessionService.getErpSid();
        SalarySlip salarySlip = salarySlipService.getSalarySlipByName(sid, salarySlipName);
        Employee employee = employeeService.getEmployeeByName(sid,salarySlip.getEmployee());
        salarySlip.setEmployeeObject(employee);

        byte[] pdfBytes = exportPdfService.generateSalarySlipPdf(salarySlip);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=test-ui.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/statistics")
    public String statistics(Model model, @RequestParam(name = "year", required = false) Integer year) throws JsonProcessingException {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);
        String sid = sessionService.getErpSid();
        Map<String,Object> salaryRegisters = salaryRegisterService.getAllSalaryRegisterByYearGroupByMonth(sid,year);
        Map<String,Object> data = salaryRegisterService.getDataChart(salaryRegisters);
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(data);
        model.addAttribute("jsonData", jsonData);
        model.addAttribute("salaryRegisters", salaryRegisters);
        return "page/hr/statistics";
    }

}
