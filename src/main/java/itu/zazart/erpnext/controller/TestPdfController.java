package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.service.hr.ExportPdfService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class TestPdfController {

    @Autowired
    private ExportPdfService pdfExportService;

    @GetMapping("/testpdf")
    public ResponseEntity<byte[]> getTestPdf() throws Exception {
        byte[] pdfBytes = pdfExportService.generateTodoListPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "todo_list.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/testUI")
    public ResponseEntity<byte[]> getTestUI() throws Exception {
        byte[] pdfBytes = pdfExportService.generateModernPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=test-ui.pdf"); // syntaxe complète ici

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

}
