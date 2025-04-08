package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.DssService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/department-of-student-services")
@RequiredArgsConstructor
public class DssController {

    private final DssService service;
    private final BaseUserService baseUserService;


    @PostMapping
    public ResponseEntity<DSS> createUser(@RequestBody DSS user) {
        DSS created = service.createUser(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DSS> updateUser(@PathVariable UUID id,
                                          @RequestBody DSS user) {
        DSS updated = service.updateUser(id, user);
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
    public ResponseEntity<List<DSS>> getAllUsers() {
        List<DSS> users = baseUserService.findAllByType(DSS.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DSS>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<DSS> users = baseUserService.findAllByType(DSS.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role")
    public ResponseEntity<List<DSS>> getUsersByRole(@RequestParam
                                                    DepartmentOfStudentServicesRole role) {
        List<DSS> users = service.findDSSByRole(role);
        return ResponseEntity.ok(users);
    }
}
