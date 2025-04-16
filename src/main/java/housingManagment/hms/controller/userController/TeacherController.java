package housingManagment.hms.controller.userController;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@Tag(name = "Teacher Management")
public class TeacherController {

    private final TeacherService service;
    private final BaseUserService baseUserService;



    @GetMapping("/{id}")
    @Operation (summary = "Get Teacher by ID", description = "Fetches the Teacher details for the given Teacher ID")
    public ResponseEntity<Optional<BaseUser>> getUserById(@PathVariable UUID id) {
        Optional<BaseUser> user = baseUserService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation (summary = "Get All Teachers", description = "Fetches the list of all Teachers")
    public ResponseEntity<List<Teacher>> getAllUsers() {
        List<Teacher> users = baseUserService.findAllByType(Teacher.class);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation (summary = "Search Teachers", description = "Searches for Teachers by name or last name using a keyword")
    public ResponseEntity<List<Teacher>> searchUsersByNameOrLastName(@RequestParam String keyword) {
        List<Teacher> users = baseUserService.findAllByType(Teacher.class).stream()
                .filter(s -> (s.getFirstName() + " " + s.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/nuid/{nuid}")
    @Operation(summary = "Get User by NUID", description = "Fetches the user details for the given NUID")
    public ResponseEntity<BaseUser> getUserByNuid(@PathVariable String nuid) {
        BaseUser user = baseUserService.findByNuid(nuid);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/school")
    @Operation (summary = "Get Teachers by School", description = "Fetches the list of Teachers with the specified role")
    public ResponseEntity<List<Teacher>> getUsersBySchool(@RequestParam TeacherPosition pos) {
        List<Teacher> users = service.findTeachersByRole(pos);
        return ResponseEntity.ok(users);
    }
}
