package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.service.userService.DssService;
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
@RequestMapping("/api/dss")
@RequiredArgsConstructor
@Tag(name = "DSS Management", description = "APIs for managing DSS information")
public class DssController {

    private final DssService service;

    @PostMapping
    @Operation(summary = "Create a DSS user", description = "Creates a new DSS user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "DSS user already exists")
    })
    public ResponseEntity<DSS> createUser(@RequestBody DSS user) {
        DSS created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get DSS user by ID", description = "Retrieves a DSS user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS user found"),
            @ApiResponse(responseCode = "404", description = "DSS user not found")
    })
    public ResponseEntity<DSS> getUserById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a DSS user", description = "Updates an existing DSS user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS user updated successfully"),
            @ApiResponse(responseCode = "404", description = "DSS user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<DSS> updateUser(@PathVariable UUID id, @RequestBody DSS user) {
        DSS updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a DSS user", description = "Deletes a DSS user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS user deleted successfully"),
            @ApiResponse(responseCode = "404", description = "DSS user not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-role")
    @Operation(summary = "Get DSS users by role", description = "Retrieves DSS users filtered by their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS users found")
    })
    public ResponseEntity<List<DSS>> getUsersByRole(@RequestParam DepartmentOfStudentServicesRole role) {
        List<DSS> users = service.findDSSByRole(role);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/search")
    @Operation(summary = "Search DSS users", description = "Searches for DSS users by name or last name using a keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS users found")
    })
    public ResponseEntity<List<DSS>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<DSS> users = service.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count-by-role")
    @Operation(summary = "Count DSS users by role", description = "Returns the count of DSS users by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByRole() {
        long count = service.countByRole();
        return ResponseEntity.ok(count);
    }

}