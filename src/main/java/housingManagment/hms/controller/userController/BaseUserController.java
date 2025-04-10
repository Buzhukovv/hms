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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Base User Management", description = "APIs for managing all user types")
public class BaseUserController {

    private final BaseUserService baseUserService;
    @Getter
    public static class CreateUserRequest {
        private String userType; // "Student", "Teacher", etc.
        private String firstName;
        private String lastName;
        private String middleName;
        private int nationalId;
        private int nuid;
        private int identityDocNo;
        private LocalDate identityIssueDate;
        private String email;
        private String localPhone;
        private String password;
        private String vehicle;
        private Gender gender;
        private HousingManagementRole hm_role;
        private DepartmentOfStudentServicesRole dss_role;
        private String block;
        private MaintenanceRole m_role;
        private String relation;
        private BaseUser mainUser ;
        private SchoolsAndSpecialties school;
        private TeacherPosition position;
        private String specialty;
        private StudentRole s_role;
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user of any type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<BaseUser> createUser(@RequestBody CreateUserRequest request) {
        BaseUser user;

        // ✅ Use enhanced switch with correct scoping
        switch (request.getUserType()) {
            case "Student" -> {
                Student student = new Student();
                student.setSchool(request.getSchool());
                student.setSpecialty(request.getSpecialty());
                student.setRole(request.getS_role());
                user = student;
            }
            case "Teacher" -> {
                Teacher teacher = new Teacher();
                teacher.setPosition(request.getPosition());
                teacher.setSchool(request.getSchool());
                user = teacher;
            }
            case "DSS" -> {
                DSS dss = new DSS();
                dss.setRole(request.getDss_role());
                user = dss;
            }
            case "Maintenance" -> {
                Maintenance maintenance = new Maintenance();
                maintenance.setRole(request.getM_role());
                user = maintenance;
            }
            case "FamilyMember" -> {
                FamilyMember fm = new FamilyMember();
                fm.setRelation(request.getRelation());
                fm.setMainUser(request.getMainUser());
                user = fm;
            }
            case "HousingManagement" -> {
                HousingManagement hm = new HousingManagement();
                hm.setRole(request.getHm_role());
                hm.setBlock(request.getBlock());
                user = hm;
            }
            default -> throw new IllegalArgumentException("Unsupported user type: " + request.getUserType());
        }

        // ✅ Set shared base fields after instantiating the correct object
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setNationalId(request.getNationalId());
        user.setNuid(request.getNuid());
        user.setIdentityDocNo(request.getIdentityDocNo());
        user.setIdentityIssueDate(request.getIdentityIssueDate());
        user.setLocalPhone(request.getLocalPhone());
        user.setPassword(request.getPassword());
        user.setVehicle(request.getVehicle());
        user.setGender(request.getGender());
        user.setEmail(request.getEmail());

        // ✅ Save the constructed user
        BaseUser created = baseUserService.save(user);
        return ResponseEntity.ok(created);
    }


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

    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates an existing user of any type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<BaseUser> updateUser(@PathVariable UUID id, @RequestBody BaseUser user) {
        BaseUser updated = baseUserService.updateUser(id, user);
        return ResponseEntity.ok(updated);
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
    public ResponseEntity<BaseUser> getUserByNuid(@RequestParam int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-national-id")
    @Operation(summary = "Get user by national ID", description = "Retrieves a user by their national ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<BaseUser> getUserByNationalId(@RequestParam int nationalId) {
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

    @GetMapping("/count-all-tenant-types")
    @Operation(summary = "Count all tenant types", description = "Returns a map of tenant types and their counts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Counts retrieved successfully")
    })
    public ResponseEntity<Map<String, Object>> countAllTenantTypes() {
        Map<String, Object> counts = baseUserService.countAllTenantTypes();
        return ResponseEntity.ok(counts);
    }
}