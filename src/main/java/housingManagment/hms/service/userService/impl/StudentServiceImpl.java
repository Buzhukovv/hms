package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.Student;
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
        return repository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersByRole(String role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySchool(String school) {
        return repository.findAll().stream()
                .filter(user -> user.getSchool().equalsIgnoreCase(school))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySpecialty(String specialty) {
        return repository.findAll().stream()
                .filter(user -> user.getSpecialty().equalsIgnoreCase(specialty))
                .collect(Collectors.toList());
    }
}
