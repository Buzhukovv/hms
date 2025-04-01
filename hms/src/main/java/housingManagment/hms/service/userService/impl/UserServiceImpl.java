package housingManagment.hms.service.userService.impl;

import housingManagment.hms.dto.UserListDTO;
import housingManagment.hms.entities.userEntity.*;
import housingManagment.hms.enums.userEnum.*;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.userRepository.*;
import housingManagment.hms.service.userService.UserService;
import housingManagment.hms.util.PasswordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    @Transactional(readOnly = true)
    public Page<UserListDTO> getAllUsersFiltered(
            String userType,
            String role,
            String school,
            String searchTerm,
            String sortBy,
            String sortDirection,
            Pageable pageable) {

        List<BaseUser> users = userRepository.findAll();

        // Apply filters
        Stream<BaseUser> userStream = users.stream();

        if (userType != null && !userType.isEmpty()) {
            userStream = userStream.filter(user -> getUserType(user).equals(userType));
        }

        if (role != null && !role.isEmpty()) {
            userStream = userStream.filter(user -> getUserRole(user).equals(role));
        }

        if (school != null && !school.isEmpty()) {
            userStream = userStream.filter(user -> getUserSchool(user).equals(school));
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            userStream = userStream.filter(user -> user.getFirstName().toLowerCase().contains(searchLower) ||
                    user.getLastName().toLowerCase().contains(searchLower) ||
                    user.getEmail().toLowerCase().contains(searchLower));
        }

        // Convert to DTOs
        List<UserListDTO> dtos = userStream
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Apply sorting
        if (sortBy != null && sortDirection != null) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            dtos = sortUsers(dtos, sortBy, direction);
        }

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        return new PageImpl<>(
                dtos.subList(start, end),
                pageable,
                dtos.size());
    }

    @Override
    public List<String> getAllUserTypes() {
        return List.of("Student", "Teacher", "DSS", "Maintenance", "HousingManagement");
    }

    @Override
    public Map<String, List<String>> getRolesByUserType(String userType) {
        return switch (userType) {
            case "Student" -> Map.of("Student", Arrays.stream(StudentRole.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            case "Teacher" -> Map.of("Teacher", Arrays.stream(TeacherPosition.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            case "DSS" -> Map.of("DSS", Arrays.stream(DepartmentOfStudentServicesRole.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            case "Maintenance" -> Map.of("Maintenance", Arrays.stream(MaintenanceRole.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            case "HousingManagement" -> Map.of("HousingManagement", Arrays.stream(HousingManagementRole.values())
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            default -> Map.of();
        };
    }

    @Override
    public List<String> getAllSchools() {
        return Arrays.stream(SchoolsAndSpecialties.values())
                .map(SchoolsAndSpecialties::getDisplayName)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Count by user type
        Map<String, Long> userTypeCounts = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        this::getUserType,
                        Collectors.counting()));
        stats.put("userTypeCounts", userTypeCounts);

        // Count by school
        Map<String, Long> schoolCounts = userRepository.findAll().stream()
                .filter(user -> user instanceof Student || user instanceof Teacher)
                .collect(Collectors.groupingBy(
                        this::getUserSchool,
                        Collectors.counting()));
        stats.put("schoolCounts", schoolCounts);

        return stats;
    }

    @Override
    public void exportUsers(String userType, String role, String school, String searchTerm) {
        // Implementation for exporting users to CSV/Excel
        // This would typically use a library like Apache POI for Excel export
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

    // Helper methods
    private UserListDTO convertToDTO(BaseUser user) {
        UserListDTO dto = new UserListDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setLocalPhone(user.getLocalPhone());
        dto.setUserType(getUserType(user));
        dto.setRole(getUserRole(user));
        dto.setSchool(getUserSchool(user));
        dto.setSpecialty(user instanceof Student ? ((Student) user).getSpecialty() : null);
        dto.setGender(user.getGender());
        dto.setNuid(user.getNuid());
        dto.setIdentityIssueDate(user.getIdentityIssueDate());
        dto.setStatus("Active"); // You might want to add a status field to BaseUser
        return dto;
    }

    private String getUserType(BaseUser user) {
        if (user instanceof Student)
            return "Student";
        if (user instanceof Teacher)
            return "Teacher";
        if (user instanceof DSS)
            return "DSS";
        if (user instanceof Maintenance)
            return "Maintenance";
        if (user instanceof HousingManagement)
            return "HousingManagement";
        return "Unknown";
    }

    private String getUserRole(BaseUser user) {
        if (user instanceof Student)
            return ((Student) user).getRole().name();
        if (user instanceof Teacher)
            return ((Teacher) user).getPosition().name();
        if (user instanceof DSS)
            return ((DSS) user).getRole().name();
        if (user instanceof Maintenance)
            return ((Maintenance) user).getRole().name();
        if (user instanceof HousingManagement)
            return ((HousingManagement) user).getRole().name();
        return "Unknown";
    }

    private String getUserSchool(BaseUser user) {
        if (user instanceof Student)
            return ((Student) user).getSchool().getDisplayName();
        if (user instanceof Teacher)
            return ((Teacher) user).getSchool().getDisplayName();
        return "N/A";
    }

    private List<UserListDTO> sortUsers(List<UserListDTO> users, String sortBy, Sort.Direction direction) {
        Comparator<UserListDTO> comparator = switch (sortBy) {
            case "firstName" -> Comparator.comparing(UserListDTO::getFirstName);
            case "lastName" -> Comparator.comparing(UserListDTO::getLastName);
            case "email" -> Comparator.comparing(UserListDTO::getEmail);
            case "userType" -> Comparator.comparing(UserListDTO::getUserType);
            case "role" -> Comparator.comparing(UserListDTO::getRole);
            case "school" -> Comparator.comparing(UserListDTO::getSchool);
            default -> Comparator.comparing(UserListDTO::getLastName);
        };

        if (direction == Sort.Direction.DESC) {
            comparator = comparator.reversed();
        }

        return users.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}