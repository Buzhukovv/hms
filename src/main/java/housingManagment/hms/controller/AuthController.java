package housingManagment.hms.controller;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.service.userService.BaseUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final BaseUserService baseUserService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;
//    private final PasswordEncoder passwordEncoder;

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage() {
        // If user is already authenticated, redirect to dashboard
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
//            return "redirect:/";
//        }
        return "auth/login";
    }
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        Optional<BaseUser> optionalUser = Optional.ofNullable(baseUserService.findByEmail(email));

        if (optionalUser.isPresent()) {
            BaseUser user = optionalUser.get();

            if (password.equals(user.getPassword())) {
                session.setAttribute("loggedInUser", user);
                return "redirect:/";
            }
        }

        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        return "redirect:/login";
    }


}