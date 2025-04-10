package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.service.LeaseService;
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
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/students")
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

    @GetMapping("/")
    @Operation(summary = "Get all students", description = "Retrieves all students from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List rendered successfully")
    })
    public List<Student> showAllStudents() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieves a student's information by their ID (JSON response)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<Student> getStudentById(@PathVariable UUID id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

    @GetMapping("/{id}/view")
    @Operation(summary = "View student details", description = "Renders a view of a student's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View rendered successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public String viewStudent(@PathVariable UUID id, Model model) {
        Student student = studentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        model.addAttribute("student", student);

        List<Lease> leases = leaseService.getLeasesByTenant(id);
        for (Lease lease : leases) {
            if (lease.getProperty() != null) {
                lease.getProperty().getPropertyNumber();
                lease.getProperty().getPropertyBlock();
            }
        }
        model.addAttribute("leases", leases);

        return "students/view";
    }

    @GetMapping("/list")
    @Operation(summary = "List all students", description = "Renders a paginated list of students with filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List rendered successfully")
    })
    public String listStudents(Model model,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String role,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        List<Student> allStudents = studentService.findAll();

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

        int totalItems = allStudents.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / size));
        page = Math.max(0, Math.min(page, totalPages - 1));

        List<Student> pagedStudents;
        if (totalItems > 0) {
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            pagedStudents = allStudents.subList(fromIndex, toIndex);
        } else {
            pagedStudents = new ArrayList<>();
        }

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found")
    })
    public ResponseEntity<List<Student>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Student> users = studentService.findAll().stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    @Operation(summary = "Get students by role", description = "Retrieves students filtered by their role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found")
    })
    public ResponseEntity<List<Student>> getUsersByRole(@RequestParam StudentRole role) {
        List<Student> users = studentService.findStudentsByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    @Operation(summary = "Get students by school", description = "Retrieves students filtered by their school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found")
    })
    public ResponseEntity<List<Student>> getUsersBySchool(@RequestParam SchoolsAndSpecialties school) {
        List<Student> users = studentService.getUsersBySchool(school);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/specialty")
    @Operation(summary = "Get students by specialty", description = "Retrieves students filtered by their specialty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students found")
    })
    public ResponseEntity<List<Student>> getUsersBySpecialty(@RequestParam String specialty) {
        List<Student> users = studentService.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/roommates")
    @Operation(summary = "Get student's current roommates", description = "Retrieves the current roommates of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roommates retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<List<Student>> getRoommates(@PathVariable UUID id) {
        List<Student> roommates = studentService.getRoommates(id);
        return ResponseEntity.ok(roommates);
    }

    @GetMapping("/{id}/ex-roommates")
    @Operation(summary = "Get student's ex-roommates", description = "Retrieves the historical roommates of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ex-roommates retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<List<Student>> getExRoommates(@PathVariable UUID id) {
        List<Student> exRoommates = studentService.getExRoommates(id);
        return ResponseEntity.ok(exRoommates);
    }

    @GetMapping("/{id}/leases")
    @Operation(summary = "Get student's leases", description = "Retrieves the current and past leases of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leases retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<List<Lease>> getLeases(@PathVariable UUID id) {
        List<Lease> leases = leaseService.getLeasesByTenant(id);
        return ResponseEntity.ok(leases);
    }

    @PatchMapping("/{id}/change-role")
    @Operation(summary = "Change student's role", description = "Updates the role of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "400", description = "Invalid role")
    })
    public ResponseEntity<Student> changeRole(@PathVariable UUID id, @RequestParam StudentRole role) {
        Student updated = studentService.changeRole(id, role);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/change-school")
    @Operation(summary = "Change student's school", description = "Updates the school of a student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "School updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "400", description = "Invalid school")
    })
    public ResponseEntity<Student> changeSchool(@PathVariable UUID id, @RequestParam SchoolsAndSpecialties school) {
        Student updated = studentService.changeSchool(id, school);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/count-by-role")
    @Operation(summary = "Count students by role", description = "Returns the count of students by role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countByRole() {
        long count = studentService.countByRole();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-school")
    @Operation(summary = "Count students by school", description = "Returns the count of students by school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countBySchool() {
        long count = studentService.countBySchool();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count-by-specialty")
    @Operation(summary = "Count students by specialty", description = "Returns the count of students by specialty")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countBySpecialty() {
        long count = studentService.countBySpecialty();
        return ResponseEntity.ok(count);
    }
}