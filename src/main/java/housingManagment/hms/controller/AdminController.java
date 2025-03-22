package housingManagment.hms.controller;

import housingManagment.hms.service.impl.LeaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LeaseServiceImpl leaseService;

    /**
     * Synchronize all property statuses based on active leases.
     * Only accessible to administrators.
     */
    @PostMapping("/sync-property-statuses")
    public ResponseEntity<String> synchronizePropertyStatuses() {
        try {
            leaseService.synchronizeAllPropertyStatuses();
            return ResponseEntity.ok("Property statuses successfully synchronized based on active leases.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error synchronizing property statuses: " + e.getMessage());
        }
    }
}