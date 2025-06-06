package itu.zazart.erpnext.controller;

import itu.zazart.erpnext.model.User;
import itu.zazart.erpnext.service.AuthService;
import itu.zazart.erpnext.service.SessionService;
import itu.zazart.erpnext.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final AuthService authService;
    private final UserService userService;
    private final SessionService sessionService;

    public HomeController(AuthService authService, UserService userService, SessionService sessionService) {
        this.authService = authService;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        if (!sessionService.isLoggedIn()) {
            return "redirect:/";
        }
        User user = sessionService.getErpUser();
        model.addAttribute("user", user);
        return "page/home";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String usr,
            @RequestParam String pwd,
            RedirectAttributes redirectAttributes
    ) {
        boolean loginOk = authService.authenticate(usr, pwd);
        String sid = sessionService.getErpSid();
        User user = userService.checkUser(usr, sid);
        sessionService.setUserSession(user);

        if (loginOk) {
            return "redirect:/employee";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid Login. Try again.");
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
