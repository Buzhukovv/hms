package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.HousingManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/housing-management")
@RequiredArgsConstructor
public class HousingManagementController {

    private final HousingManagementService service;
    private final BaseUserService baseUserService;

    @PostMapping
    public ResponseEntity<HousingManagement> createUser(@RequestBody HousingManagement user) {
        HousingManagement created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HousingManagement> updateUser(@PathVariable UUID id,
                                                        @RequestBody HousingManagement user) {
        HousingManagement updated = service.updateUser(id, user);
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
    public ResponseEntity<List<HousingManagement>> getAllUsers() {
        List<HousingManagement> users = baseUserService.findAllByType(HousingManagement.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HousingManagement>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<HousingManagement> users = baseUserService.findAllByType(HousingManagement.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    public ResponseEntity<List<HousingManagement>> getUsersByRole(@RequestParam HousingManagementRole role) {
        List<HousingManagement> users = service.findHousingManagementByRole(role);
        return ResponseEntity.ok(users);
    }
}
