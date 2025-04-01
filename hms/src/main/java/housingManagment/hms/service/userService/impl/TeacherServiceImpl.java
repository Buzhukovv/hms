package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.service.userService.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;

    @Override
    public Teacher createUser(Teacher user) {
        return repository.save(user);
    }

    @Override
    public Teacher updateUser(UUID id, Teacher user) {
        Teacher existingUser = getUserById(id);
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
        existingUser.setSchool(user.getSchool());
        existingUser.setGender(user.getGender());
        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        Teacher user = getUserById(id);
        repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> searchUsersByNameOrLastName(String keyword) {
        return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getUsersBySchool(String school) {
        try {
            SchoolsAndSpecialties schoolEnum = SchoolsAndSpecialties.fromDisplayName(school);
            return repository.findBySchool(schoolEnum);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list if the school name is invalid
        }
    }
}
