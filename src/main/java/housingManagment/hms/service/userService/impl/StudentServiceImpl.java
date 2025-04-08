package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Student createUser(Student user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public Student updateUser(UUID id, Student user) {
        // Find the existing user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a Student instance
        if (!(baseUser instanceof Student existingUser)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student user");
        }

        // Update fields
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

        // Save and return the updated user
        return repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a Student instance
        if (!(baseUser instanceof Student user)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student user");
        }

        // Delete the user
        repository.delete(user);
    }


    @Override
    public List<Student> findStudentsByRole(StudentRole role) {
        return baseUserService.findAllByType(Student.class).stream()
                .filter(s -> s.getRole() == role)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Student> getUsersBySchool(
            SchoolsAndSpecialties school) {
        return repository.findBySchool(school);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySpecialty(String specialty) {
        return repository.findBySpecialty(specialty);
    }

}