package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.StudentService;

import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing user information")
public class StudentController {

    private final StudentService studentService;
    private final LeaseService leaseService;
    private final BaseUserService baseUserService;
    @PostMapping
    @Operation(summary = "Create a new student", description = "Creates a new student with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Student already exists")
    })
    public ResponseEntity<Student> createUser(@RequestBody Student user) {
        Student created = studentService.createUser(user);
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
        Student updated = studentService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student", description = "Deletes a student by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Student ID") @PathVariable UUID id) {
        studentService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable int nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name or last name")
    public ResponseEntity<List<Student>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Student> users = baseUserService.findAllByType(Student.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get students by role", description = "Retrieves students filtered by their role")
    public ResponseEntity<List<Student>> getUsersByRole(@RequestParam StudentRole role) {
        List<Student> users = studentService.findStudentsByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Student>> getUsersBySchool(@RequestParam SchoolsAndSpecialties school) {
        List<Student> users = studentService.getUsersBySchool(school);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/specialty")
    public ResponseEntity<List<Student>> getUsersBySpecialty(@RequestParam String specialty) {
        List<Student> users = studentService.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(users);
    }
}
