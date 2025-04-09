package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.entities.userEntity.Teacher;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO : Check all of the endpoints and add if business logic needs new endpoints



@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Student createUser(Student user) {
        return (Student) repository.save(user);
    }

    @Override
    @Transactional
    public Student updateUser(UUID id, Student user) {
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        // Verify that the user is a Student instance
        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        Student existingUser = (Student) baseUser.get();

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
        return (Student) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        Student existingUser = (Student) baseUser.get();

        // Delete the user
        repository.delete(existingUser);
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