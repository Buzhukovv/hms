package housingManagment.hms.controller;

import housingManagment.hms.dto.UserListDTO;
import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.*;
import housingManagment.hms.service.LeaseService;
import housingManagment.hms.service.MaintenanceRequestService;
import housingManagment.hms.service.property.*;
import housingManagment.hms.service.userService.StudentService;
import housingManagment.hms.service.userService.TeacherService;
import housingManagment.hms.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final UserRepository userRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;

    @GetMapping("/users")
    public String listAllUsers(Model model,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String userType,
                               @RequestParam(required = false) String role,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> userListData = userService.getUserListData(search, userType, role, page, size);
        model.addAllAttributes(userListData);
        return "users/list";
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