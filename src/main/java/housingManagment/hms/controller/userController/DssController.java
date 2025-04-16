package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.service.userService.DssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Создать DSS пользователя", description = "Создает нового пользователя службы поддержки студентов (DSS)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
    })
    public ResponseEntity<DSS> createUser(@RequestBody DSS user) {
        DSS createdUser = service.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all DSS users", description = "Retrieves all Department of Student Services (DSS) users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DSS users retrieved successfully")
    })
    public ResponseEntity<List<DSS>> getAllDssUsers() {
        List<DSS> allUsers = service.findAll();
        return ResponseEntity.ok(allUsers);
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
    @GetMapping("/count-all")
    @Operation(summary = "Count DSS users by role", description = "Returns the count of DSS users by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countAllMember() {
        long count = service.countByAll();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-role")
    @Operation(summary = "Count DSS users by role", description = "Returns the count of DSS users by specified role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role parameter")
    })
    public ResponseEntity<Long> countByRole(@RequestParam DepartmentOfStudentServicesRole role) {
        long count = service.countByRole(role);
        return ResponseEntity.ok(count);
    }


}