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
import housingManagment.hms.service.userService.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage() {
        // If user is already authenticated, redirect to dashboard
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        return "auth/login";
    }

    /**
     * Display user profile page
     */
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        try {
            BaseUser user = userService.getUserByEmail(email);
            model.addAttribute("user", user);

            // Set user's full name in session for sidebar display
            String fullName = user.getFirstName() + " " + user.getLastName();
            session.setAttribute("userFullName", fullName);

            // Check user type and add specific attributes
            if (user instanceof Student) {
                model.addAttribute("userType", "student");
                model.addAttribute("student", user);
            } else if (user instanceof Teacher) {
                model.addAttribute("userType", "teacher");
                model.addAttribute("teacher", user);
            } else if (user instanceof Maintenance) {
                model.addAttribute("userType", "maintenance");
                model.addAttribute("maintenance", user);
            } else if (user instanceof HousingManagement) {
                model.addAttribute("userType", "housingManagement");
                model.addAttribute("housingManagement", user);
            } else if (user instanceof DSS) {
                model.addAttribute("userType", "dss");
                model.addAttribute("dss", user);
            }

            return "auth/profile";
        } catch (Exception e) {
            log.error("Error loading profile for user {}: {}", email, e.getMessage());
            return "redirect:/login";
        }
    }

    /**
     * Update user profile
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("middleName") String middleName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("localPhone") String localPhone,
            @RequestParam(value = "school", required = false) SchoolsAndSpecialties school,
            @RequestParam(value = "specialty", required = false) String specialty,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();

        try {
            BaseUser currentUser = userService.getUserByEmail(currentEmail);

            // Update common fields
            currentUser.setFirstName(firstName);
            currentUser.setMiddleName(middleName.isEmpty() ? null : middleName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            currentUser.setLocalPhone(localPhone.isEmpty() ? null : localPhone);

            // Update specific fields based on user type
            if (currentUser instanceof Student && school != null && specialty != null) {
                Student student = (Student) currentUser;
                student.setSchool(school);
                student.setSpecialty(specialty);
            } else if (currentUser instanceof Teacher && school != null) {
                Teacher teacher = (Teacher) currentUser;
                teacher.setSchool(school);
            }

            // Save the updated user
            BaseUser updatedUser = userService.updateUser(currentUser.getId(), currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
            return "redirect:/profile";

        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    /**
     * Change password
     */
    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/profile";
        }

        try {
            boolean success = userService.changePassword(email, currentPassword, newPassword);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
            }

            return "redirect:/profile";

        } catch (Exception e) {
            log.error("Error changing password: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change password: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}