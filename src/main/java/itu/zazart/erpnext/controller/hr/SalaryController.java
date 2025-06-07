package itu.zazart.erpnext.controller.hr;

import itu.zazart.erpnext.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class SalaryController {
    private final SessionService sessionService;
    private static final Logger logger = LoggerFactory.getLogger(SalaryController.class);

    public SalaryController(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
