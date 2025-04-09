package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class MaintenanceController {

    private final MaintenanceService service;

    private final BaseUserService baseUserService;

    @PostMapping
    @Operation(summary = "Create Maintenance User", description = "Creates a new user in the Maintenance")
    public ResponseEntity<Maintenance> createUser(@RequestBody Maintenance user) {
        Maintenance created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Maintenance User", description = "Updates the user details for the given user ID")
    public ResponseEntity<Maintenance> updateUser(@PathVariable UUID id,
                                                  @RequestBody Maintenance user) {
        Maintenance updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Maintenance User", description = "Deletes the user associated with the given user ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Maintenance User by ID", description = "Fetches the user details for the given user ID")
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }


    @GetMapping
    @Operation(summary = "Get All Maintenance Users", description = "Fetches the list of all users in the Maintenance")
    public ResponseEntity<List<Maintenance>> getAllUsers() {
        List<Maintenance> users = baseUserService.findAllByType(Maintenance.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search Users", description = "Searches for users by name or last name using a keyword")
    public ResponseEntity<List<Maintenance>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Maintenance> users = baseUserService.findAllByType(Maintenance.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get Maintenance Users by Role", description = "Fetches the list of users with the specified role")
    public ResponseEntity<List<Maintenance>> getUsersByRole(@RequestParam
                                                            MaintenanceRole role) {
        List<Maintenance> users = service.findMaintenanceByRole(role);
        return ResponseEntity.ok(users);
    }
}
