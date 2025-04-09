package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.HousingManagementService;
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
@RequestMapping("/api/housing-management")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class HousingManagementController {

    private final HousingManagementService service;
    private final BaseUserService baseUserService;

    @PostMapping
    @Operation(summary = "Create Housing Management User", description = "Creates a new Housing Management user in the Housing Management")
    public ResponseEntity<HousingManagement> createUser(@RequestBody HousingManagement user) {
        HousingManagement created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Housing Management User", description = "Updates the user details for the given user ID")
    public ResponseEntity<HousingManagement> updateUser(@PathVariable UUID id,
                                                        @RequestBody HousingManagement user) {
        HousingManagement updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Housing Management User", description = "Deletes the user associated with the given user ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Housing Management User by ID", description = "Fetches the user details for the given user ID")
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get All Housing Management Users", description = "Fetches the list of all users in the Housing Management")
    public ResponseEntity<List<HousingManagement>> getAllUsers() {
        List<HousingManagement> users = baseUserService.findAllByType(HousingManagement.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search Housing Management Users", description = "Searches for users by name or last name using a keyword")
    public ResponseEntity<List<HousingManagement>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<HousingManagement> users = baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role")
    @Operation(summary = "Get Housing Management Users by Role", description = "Fetches the list of users with the specified role")
    public ResponseEntity<List<HousingManagement>> getUsersByRole(@RequestParam HousingManagementRole role) {
        List<HousingManagement> users = service.findHousingManagementByRole(role);
        return ResponseEntity.ok(users);
    }
}
