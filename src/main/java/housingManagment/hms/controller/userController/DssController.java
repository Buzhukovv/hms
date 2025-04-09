package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.DssService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/department-of-student-services")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class DssController {

    private final DssService service;
    private final BaseUserService baseUserService;


    @Operation(summary = "Create DSS User", description = "Creates a new DSS user")
    @PostMapping
    public ResponseEntity<DSS> createUser(@RequestBody DSS user) {
        DSS created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update DSS User", description = "Updates the DSS details for the given DSS ID")
    public ResponseEntity<DSS> updateUser(@PathVariable UUID id,
                                          @RequestBody DSS user) {
        DSS updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete DSS User", description = "Deletes the DSS associated with the given DSS ID")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get DSS User by ID", description = "Fetches the DSS details for the given DSS ID")
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get All DSS Users", description = "Fetches the list of all DSS Users")
    public ResponseEntity<List<DSS>> getAllUsers() {
        List<DSS> users = baseUserService.findAllByType(DSS.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search DSS Users", description = "Searches for DSS users by name or last name using a keyword")

    public ResponseEntity<List<DSS>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<DSS> users = baseUserService.findAllByType(DSS.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get DSS Users by Role", description = "Fetches the list of users with the specified role")

    public ResponseEntity<List<DSS>> getUsersByRole(@RequestParam
                                                    DepartmentOfStudentServicesRole role) {
        List<DSS> users = service.findDSSByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }
}
