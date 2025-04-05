package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.service.userService.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    @PostMapping
    public ResponseEntity<Teacher> createUser(@RequestBody Teacher user) {
        Teacher created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateUser(@PathVariable UUID id,
                                              @RequestBody Teacher user) {
        Teacher updated = service.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getUserById(@PathVariable UUID id) {
        Teacher user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllUsers() {
        List<Teacher> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Teacher>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Teacher> users = service.searchUsersByNameOrLastName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Teacher>> getUsersBySchool(@RequestParam String school) {
        List<Teacher> users = service.getUsersBySchool(school);
        return ResponseEntity.ok(users);
    }
}
