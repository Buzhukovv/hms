package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Teacher createUser(Teacher user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public Teacher updateUser(UUID id, Teacher user) {
        // Find the existing user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a Teacher instance
        if (!(baseUser instanceof Teacher existingUser)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher user");
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
        existingUser.setPosition(user.getPosition());

        // Save and return the updated user
        return repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a Teacher instance
        if (!(baseUser instanceof Teacher user)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher user");
        }

        // Delete the user
        repository.delete(user);
    }


    @Override
    public List<Teacher> findTeachersByRole(TeacherPosition pos) {
        return baseUserService.findAllByType(Teacher.class).stream()
                .filter(t -> t.getPosition() == pos)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Teacher> getUsersBySchool(
            SchoolsAndSpecialties school) {
        return repository.findBySchool(school);
    }

}
