package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.service.userService.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing student information")
public class StudentController {

    private final StudentService service;

    @PostMapping
    @Operation(summary = "Create a new student", description = "Creates a new student with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Student already exists")
    })
    public ResponseEntity<Student> createUser(@RequestBody Student user) {
        Student created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student", description = "Updates an existing student's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Student> updateUser(@Parameter(description = "Student ID") @PathVariable UUID id,
            @RequestBody Student user) {
        Student updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Deletes a student by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Student ID") @PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student's information by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Student> getUserById(@Parameter(description = "Student ID") @PathVariable UUID id) {
        Student user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieves a list of all students")
    public ResponseEntity<List<Student>> getAllUsers() {
        List<Student> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name or last name")
    public ResponseEntity<List<Student>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Student> users = service.searchUsersByNameOrLastName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get students by role", description = "Retrieves students filtered by their role")
    public ResponseEntity<List<Student>> getUsersByRole(@RequestParam String role) {
        List<Student> users = service.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Student>> getUsersBySchool(@RequestParam String school) {
        List<Student> users = service.getUsersBySchool(school);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/specialty")
    public ResponseEntity<List<Student>> getUsersBySpecialty(@RequestParam String specialty) {
        List<Student> users = service.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(users);
    }
}
