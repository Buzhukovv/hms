package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final HousingManagementRepository housingManagementRepository;
    private final DepartmentOfStudentServicesRepository dssRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to authenticate user with email: {}", email);

        Optional<? extends BaseUser> userOpt = findUserByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        BaseUser user = userOpt.get();
        log.debug("User found: {} ({})", user.getEmail(), user.getClass().getSimpleName());

        List<GrantedAuthority> authorities = getAuthoritiesForUser(user);

        // Return the user details - password validation is handled by the custom
        // AuthenticationProvider
        return new User(
                user.getEmail(),
                user.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities);
    }

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

    private List<GrantedAuthority> getAuthoritiesForUser(BaseUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add a general user role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Add specific role based on user type
        if (user instanceof Student) {
            authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ((Student) user).getRole().name()));
        } else if (user instanceof Teacher) {
            authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
        } else if (user instanceof Maintenance) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MAINTENANCE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ((Maintenance) user).getRole().name()));
        } else if (user instanceof HousingManagement) {
            authorities.add(new SimpleGrantedAuthority("ROLE_HOUSING_MANAGEMENT"));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ((HousingManagement) user).getRole().name()));

            // Managers should have admin role
            if (((HousingManagement) user).getRole().name().equals("MANAGER")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        } else if (user instanceof DSS) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DSS"));
            authorities.add(new SimpleGrantedAuthority("ROLE_DSS_STAFF"));
        }

        return authorities;
    }
}