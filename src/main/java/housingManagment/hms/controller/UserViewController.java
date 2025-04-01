package housingManagment.hms.controller;

import housingManagment.hms.dto.UserListDTO;
import housingManagment.hms.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    @GetMapping
    public Page<UserListDTO> getAllUsers(
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {
        return userService.getAllUsersFiltered(userType, role, school, searchTerm, sortBy, sortDirection, pageable);
    }

    @GetMapping("/types")
    public List<String> getUserTypes() {
        return userService.getAllUserTypes();
    }

    @GetMapping("/roles")
    public Map<String, List<String>> getRolesByUserType(@RequestParam String userType) {
        return userService.getRolesByUserType(userType);
    }

    @GetMapping("/schools")
    public List<String> getSchools() {
        return userService.getAllSchools();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getUserStatistics() {
        return userService.getUserStatistics();
    }

    @GetMapping("/export")
    public void exportUsers(
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String school,
            @RequestParam(required = false) String searchTerm) {
        userService.exportUsers(userType, role, school, searchTerm);
    }
}