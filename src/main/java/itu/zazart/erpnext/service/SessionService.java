package itu.zazart.erpnext.service;

import itu.zazart.erpnext.model.User;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final HttpSession session;

    public SessionService(HttpSession session) {
        this.session = session;
    }

    public boolean isLoggedIn() {
        boolean loggedIn = session.getAttribute("erp_user") != null;
        if (loggedIn) {
            logger.info("User is logged in.");
        } else {
            logger.warn("User is not logged in.");
        }
        return loggedIn;
    }

    public User getErpUser() {
        User user = (User) session.getAttribute("erp_user");
        if (user != null) {
            logger.info("User {} retrieved from session.", user.getUsername());
        } else {
            logger.warn("No user found in session.");
        }
        return user;
    }

    public String getErpSid() {
        String sid = (String) session.getAttribute("erp_sid");
        if (sid != null) {
            logger.info("Session ID {} retrieved.", sid);
        } else {
            logger.warn("No session ID found in session.");
        }
        return sid;
    }
}
