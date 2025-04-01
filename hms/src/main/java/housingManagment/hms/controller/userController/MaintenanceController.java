package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.service.userService.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService service;

    @PostMapping
    public ResponseEntity<Maintenance> createUser(@RequestBody Maintenance user) {
        Maintenance created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> updateUser(@PathVariable UUID id,
                                                  @RequestBody Maintenance user) {
        Maintenance updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> getUserById(@PathVariable UUID id) {
        Maintenance user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAllUsers() {
        List<Maintenance> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Maintenance>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Maintenance> users = service.searchUsersByNameOrLastName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    public ResponseEntity<List<Maintenance>> getUsersByRole(@RequestParam String role) {
        List<Maintenance> users = service.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}
