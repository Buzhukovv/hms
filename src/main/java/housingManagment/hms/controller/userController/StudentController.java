package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.service.userService.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping
    public ResponseEntity<Student> createUser(@RequestBody Student user) {
        Student created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateUser(@PathVariable UUID id,
                                              @RequestBody Student user) {
        Student updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getUserById(@PathVariable UUID id) {
        Student user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllUsers() {
        List<Student> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Student> users = service.searchUsersByNameOrLastName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
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
