package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    private final BaseUserService baseUserService;

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
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<Teacher>> getAllUsers() {
        List<Teacher> users = baseUserService.findAllByType(Teacher.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Teacher>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Teacher> users = baseUserService.findAllByType(Teacher.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/school")
    public ResponseEntity<List<Teacher>> getUsersBySchool(@RequestParam TeacherPosition pos) {
        List<Teacher> users = service.findTeachersByRole(pos);
        return ResponseEntity.ok(users);
    }
}
