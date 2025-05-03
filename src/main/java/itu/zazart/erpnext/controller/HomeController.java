package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final AuthService authService;

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "page/home";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String usr,
            @RequestParam String pwd,
            RedirectAttributes redirectAttributes
    ) {
        boolean loginOk = authService.authenticate(usr, pwd);
        if (loginOk) {
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid Login. Try again.");
            return "redirect:/";
        }
    }
}
