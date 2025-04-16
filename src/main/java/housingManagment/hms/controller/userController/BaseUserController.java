package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.service.userService.BaseUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.descriptor.jdbc.JdbcTypeFamilyInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
//todo do the application form
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Base User Management", description = "APIs for managing all user types")
public class BaseUserController {

    private final BaseUserService baseUserService;



    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user of any type by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user of any type by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        baseUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> getUserByEmail(@RequestParam String email) {
        BaseUser user = baseUserService.findByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-nuid")
    @Operation(summary = "Get user by NUID", description = "Retrieves a user by their NUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> getUserByNuid(@RequestParam String nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-national-id")
    @Operation(summary = "Get user by national ID", description = "Retrieves a user by their national ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> getUserByNationalId(@RequestParam String nationalId) {
        BaseUser user = baseUserService.findByNationalId(nationalId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-name")
    @Operation(summary = "Get users by name", description = "Retrieves users by their name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found")
    })
    public ResponseEntity<List<BaseUser>> getUsersByName(@RequestParam String name) {
        List<BaseUser> users = baseUserService.findByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<List<BaseUser>> getAllUsers() {
        List<BaseUser> users = baseUserService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    @Operation(summary = "Count all users", description = "Returns the total number of users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countUsers() {
        long count = baseUserService.count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-type")
    @Operation(summary = "Count users by type", description = "Returns the count of users for a specific type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user type")
    })
    public ResponseEntity<Long> countUsersByType(@RequestParam String userType) {
        try {
            Class<? extends BaseUser> type = (Class<? extends BaseUser>) Class.forName("housingManagment.hms.entities.userEntity." + userType);
            long count = baseUserService.countByType(type);
            return ResponseEntity.ok(count);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}