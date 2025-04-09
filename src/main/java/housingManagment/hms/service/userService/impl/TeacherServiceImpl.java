package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO : Check all of the endpoints and add if business logic needs new endpoints

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Teacher createUser(Teacher user) {
        return (Teacher) repository.save(user);
    }

    @Override
    @Transactional
    public Teacher updateUser(UUID id, Teacher user) {
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        Teacher existingUser = (Teacher) baseUser.get();

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
        return (Teacher) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        Teacher existingUser = (Teacher) baseUser.get();

        // Delete the user
        repository.delete(existingUser);
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
