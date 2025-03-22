package housingManagment.hms.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Global controller advice to add common model attributes to all views
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Add mock current user information to all views
     * This will be replaced with actual authentication when implemented
     */
    @ModelAttribute
    public void addCurrentUser(Model model) {
        Map<String, String> currentUser = new HashMap<>();
        currentUser.put("name", "John Doe");
        currentUser.put("role", "Administrator");
        currentUser.put("email", "john.doe@example.com");

        model.addAttribute("currentUser", currentUser);
    }
}