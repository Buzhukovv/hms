package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.service.userService.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    @Override
    public Student createUser(Student user) {
        return repository.save(user);
    }

    @Override
    public Student updateUser(UUID id, Student user) {
        Student existingUser = getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMiddleName(user.getMiddleName());
        existingUser.setNationalId(user.getNationalId());
        existingUser.setNuid(user.getNuid());
        existingUser.setIdentityDocNo(user.getIdentityDocNo());
        existingUser.setIdentityIssueDate(user.getIdentityIssueDate());
        existingUser.setEmail(user.getEmail());
        existingUser.setLocalPhone(user.getLocalPhone());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        existingUser.setSchool(user.getSchool());
        existingUser.setSpecialty(user.getSpecialty());
        existingUser.setGender(user.getGender());
        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        Student user = getUserById(id);
        repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Student getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchUsersByNameOrLastName(String keyword) {
        return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersByRole(String role) {
        try {
            return repository.findByRole(StudentRole.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySchool(String school) {
        try {
            SchoolsAndSpecialties schoolEnum = SchoolsAndSpecialties.fromDisplayName(school);
            return repository.findBySchool(schoolEnum);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list if the school name is invalid
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySpecialty(String specialty) {
        return repository.findBySpecialtyIgnoreCase(specialty);
    }
}
