package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.userService.StudentService;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing student information")
public class StudentController {

    private final StudentService studentService;
    private final LeaseService leaseService;
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

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student's information by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public String viewStudent(@PathVariable UUID id, Model model) {
        Student student = studentService.getUserById(id);
        model.addAttribute("student", student);

        // Get leases related to this student
        List<Lease> leases = leaseService.getLeasesByTenant(id);

        // Ensure eager loading of properties to avoid LazyInitializationException in
        // view
        for (Lease lease : leases) {
            if (lease.getProperty() != null) {
                // Access property data to force initialization
                lease.getProperty().getPropertyNumber();
                lease.getProperty().getPropertyBlock();
            }
        }

        model.addAttribute("leases", leases);

        return "students/view";
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieves a list of all students")
    public String listStudents(Model model,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String role,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {

        List<Student> allStudents = studentService.getAllUsers();

        // Apply filters if provided
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            allStudents = allStudents.stream()
                    .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        if (role != null && !role.isEmpty()) {
            allStudents = allStudents.stream()
                    .filter(s -> s.getRole().toString().equals(role))
                    .collect(Collectors.toList());
        }

        // Pagination logic
        int totalItems = allStudents.size();
        // Ensure totalPages is at least 1 to prevent errors in the template
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));

        // Ensure page is within bounds
        page = Math.max(0, Math.min(page, totalPages - 1));

        // Extract the current page items
        List<Student> pagedStudents;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            pagedStudents = allStudents.subList(fromIndex, toIndex);
        } else {
            pagedStudents = new ArrayList<>();
        }

        // Add pagination attributes to the model
        model.addAttribute("students", pagedStudents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("availableSizes", List.of(10, 20, 30, 40));

        return "students/list";
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name or last name")
    public ResponseEntity<List<Student>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Student> users = studentService.searchUsersByNameOrLastName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get students by role", description = "Retrieves students filtered by their role")
    public ResponseEntity<List<Student>> getUsersByRole(@RequestParam String role) {
        List<Student> users = studentService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Student>> getUsersBySchool(@RequestParam String school) {
        List<Student> users = studentService.getUsersBySchool(school);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/specialty")
    public ResponseEntity<List<Student>> getUsersBySpecialty(@RequestParam String specialty) {
        List<Student> users = studentService.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(users);
    }
}
