package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.service.userService.UserService;
import housingManagment.hms.util.PasswordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHelper passwordHelper;

    @Override
    @Transactional(readOnly = true)
    public BaseUser getUserByEmail(String email) {
        // Try to find the user in each repository
        Optional<? extends BaseUser> user = findUserByEmail(email);
        return user.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseUser getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public BaseUser updateUser(UUID id, BaseUser updatedUser) {
        BaseUser existingUser = getUserById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        // Update common fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setMiddleName(updatedUser.getMiddleName());
        existingUser.setLocalPhone(updatedUser.getLocalPhone());

        // Email can only be changed if the new email is not already in use
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (getUserByEmail(updatedUser.getEmail()) != null) {
                throw new IllegalArgumentException("Email already in use: " + updatedUser.getEmail());
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Type-specific updates
        if (existingUser instanceof Student && updatedUser instanceof Student) {
            Student existingStudent = (Student) existingUser;
            Student updatedStudent = (Student) updatedUser;

            existingStudent.setSchool(updatedStudent.getSchool());
            existingStudent.setSpecialty(updatedStudent.getSpecialty());

            return studentRepository.save(existingStudent);
        } else if (existingUser instanceof Teacher && updatedUser instanceof Teacher) {
            Teacher existingTeacher = (Teacher) existingUser;
            Teacher updatedTeacher = (Teacher) updatedUser;

            existingTeacher.setSchool(updatedTeacher.getSchool());

            return teacherRepository.save(existingTeacher);
        } else if (existingUser instanceof Maintenance && updatedUser instanceof Maintenance) {
            Maintenance existingMaintenance = (Maintenance) existingUser;
            // Role and other sensitive fields should not be updated by users

            return maintenanceRepository.save(existingMaintenance);
        } else if (existingUser instanceof HousingManagement && updatedUser instanceof HousingManagement) {
            HousingManagement existingHousingManagement = (HousingManagement) existingUser;
            // Role and block assignments should not be updated by users

            return housingManagementRepository.save(existingHousingManagement);
        } else if (existingUser instanceof DSS && updatedUser instanceof DSS) {
            DSS existingDSS = (DSS) existingUser;
            // Role and other sensitive fields should not be updated by users

            return dssRepository.save(existingDSS);
        }

        // For any other type, just update the base fields
        return userRepository.save(existingUser);
    }

    @Override
    public boolean changePassword(String email, String currentPassword, String newPassword) {
        Optional<? extends BaseUser> userOpt = findUserByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }

        BaseUser user = userOpt.get();

        // Use PasswordHelper to support both BCrypt and plain text passwords
        if (!passwordHelper.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Always encode the new password with BCrypt
        user.setPassword(passwordHelper.encodePassword(newPassword));

        // Save the user with the updated password
        if (user instanceof Student) {
            studentRepository.save((Student) user);
        } else if (user instanceof Teacher) {
            teacherRepository.save((Teacher) user);
        } else if (user instanceof Maintenance) {
            maintenanceRepository.save((Maintenance) user);
        } else if (user instanceof HousingManagement) {
            housingManagementRepository.save((HousingManagement) user);
        } else if (user instanceof DSS) {
            dssRepository.save((DSS) user);
        } else {
            userRepository.save(user);
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseUser> searchUsersByNameOrLastName(String keyword) {
        List<BaseUser> results = new ArrayList<>();

        // Search in each repository and combine results
        results.addAll(studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword));

        results.addAll(teacherRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword));

        results.addAll(maintenanceRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword));

        results.addAll(housingManagementRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword));

        results.addAll(dssRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                keyword, keyword));

        return results;
    }

    /**
     * Helper method to find a user by email across all user repositories
     */
    private Optional<? extends BaseUser> findUserByEmail(String email) {
        // Try to find the user in each repository
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return student;
        }


        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            return teacher;
        }

        Optional<Maintenance> maintenance = maintenanceRepository.findByEmail(email);
        if (maintenance.isPresent()) {
            return maintenance;
        }

        Optional<HousingManagement> housingManagement = housingManagementRepository.findByEmail(email);
        if (housingManagement.isPresent()) {
            return housingManagement;
        }

        Optional<DSS> dss = dssRepository.findByEmail(email);
        if (dss.isPresent()) {
            return dss;
        }

        // Fallback to the generic user repository
        return userRepository.findByEmail(email);
    }
}