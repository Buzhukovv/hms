package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.service.userService.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance Management", description = "APIs for managing maintenance staff information")
public class MaintenanceController {

    private final MaintenanceService service;

    @PostMapping
    @Operation(summary = "Create a maintenance user", description = "Creates a new maintenance user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Maintenance user already exists")
    })
    public ResponseEntity<Maintenance> createUser(@RequestBody Maintenance user) {
        Maintenance created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance user by ID", description = "Retrieves a maintenance user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance user found"),
            @ApiResponse(responseCode = "404", description = "Maintenance user not found")
    })
    public ResponseEntity<Maintenance> getUserById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a maintenance user", description = "Updates an existing maintenance user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance user updated successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Maintenance> updateUser(@PathVariable UUID id, @RequestBody Maintenance user) {
        Maintenance updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a maintenance user", description = "Deletes a maintenance user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance user deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Maintenance user not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-role")
    @Operation(summary = "Get maintenance users by role", description = "Retrieves maintenance users filtered by their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance users found")
    })
    public ResponseEntity<List<Maintenance>> getUsersByRole(@RequestParam MaintenanceRole role) {
        List<Maintenance> users = service.findMaintenanceByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search maintenance users", description = "Searches for maintenance users by name or last name using a keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Maintenance users found")
    })
    public ResponseEntity<List<Maintenance>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Maintenance> users = service.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count-by-role")
    @Operation(summary = "Count maintenance users by role", description = "Returns the count of maintenance users by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByRole() {
        long count = service.countByRole();
        return ResponseEntity.ok(count);
    }
}