package housingManagment.hms.controller;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.service.DashboardService;
import housingManagment.hms.service.userService.BaseUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard Controller", description = "APIs for dashboard management")
public class DashboardController {

    private final DashboardService dashboardService;
    private final BaseUserService baseUserService;

    @Autowired
    public DashboardController(DashboardService dashboardService, BaseUserService baseUserService) {
        this.dashboardService = dashboardService;
        this.baseUserService = baseUserService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeLeases", dashboardService.getActiveLeaseCount());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/")
    public ResponseEntity<?> getDashboard() {
        try {
            // Get the authenticated user from Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User not authenticated"));
            }

            // ✅ SAFE way: get email and load full user from UserService
            String email = authentication.getName(); // This always gives you the email
            BaseUser user = baseUserService.findByEmail(email);
            DashboardData data = dashboardService.getDashboardData(user);

            // Return the DashboardData as JSON
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            // Return a JSON error response with a 500 status code
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error loading dashboard data: " + e.getMessage()));
        }
    }

    // Helper method to create a JSON error response
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}