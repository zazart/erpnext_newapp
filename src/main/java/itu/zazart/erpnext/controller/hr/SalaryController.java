package itu.zazart.erpnext.controller.hr;

import itu.zazart.erpnext.model.hr.Employee;
import itu.zazart.erpnext.model.hr.SalarySlip;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.hr.EmployeeService;
import itu.zazart.erpnext.service.hr.ExportPdfService;
import itu.zazart.erpnext.service.hr.SalarySlipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SalaryController {
    private final SessionService sessionService;
    private final ExportPdfService exportPdfService;
    private final SalarySlipService salarySlipService;
    private final EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(SalaryController.class);

    public SalaryController(SessionService sessionService, ExportPdfService exportPdfService, SalarySlipService salarySlipService, EmployeeService employeeService) {
        this.sessionService = sessionService;
        this.exportPdfService = exportPdfService;
        this.salarySlipService = salarySlipService;
        this.employeeService = employeeService;
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
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=test-ui.pdf"); // syntaxe complète ici

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
