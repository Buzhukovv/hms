package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService service;

    private final BaseUserService baseUserService;

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
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Maintenance>> getAllUsers() {
        List<Maintenance> users = baseUserService.findAllByType(Maintenance.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Maintenance>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Maintenance> users = baseUserService.findAllByType(Maintenance.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    public ResponseEntity<List<Maintenance>> getUsersByRole(@RequestParam
                                                            MaintenanceRole role) {
        List<Maintenance> users = service.findMaintenanceByRole(role);
        return ResponseEntity.ok(users);
    }
}
