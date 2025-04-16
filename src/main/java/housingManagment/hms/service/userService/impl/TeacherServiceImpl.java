package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.repository.userRepository.TeacherRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.FamilyMemberService;
import housingManagment.hms.service.userService.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final FamilyMemberService familyMemberService;

    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Override
    public Teacher createUser(Teacher user) {
        if (user == null) {
            throw new IllegalArgumentException("Teacher cannot be null");
        }
        return (Teacher) repository.save(user);
    }

    @Override
    @Transactional
    public Teacher updateUser(UUID id, Teacher user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and user cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher user");
        }

        if (!(baseUser.get() instanceof Teacher)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher");
        }

        Teacher existingUser = (Teacher) baseUser.get();

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
        existingUser.setSchool(user.getSchool());

        return (Teacher) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher user");
        }

        if (!(baseUser.get() instanceof Teacher)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Teacher");
        }

        Teacher existingUser = (Teacher) baseUser.get();
        repository.delete(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Teacher> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isPresent() && baseUser.get() instanceof Teacher) {
            return Optional.of((Teacher) baseUser.get());
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return baseUserService.findAllByType(Teacher.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findTeachersByRole(TeacherPosition pos) {
        if (pos == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        return baseUserService.findAllByType(Teacher.class).stream()
                .filter(t -> t.getPosition() == pos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> getUsersBySchool(SchoolsAndSpecialties school) {
        if (school == null) {
            throw new IllegalArgumentException("School cannot be null");
        }
        return repository.findBySchool(school);
    }

    @Transactional(readOnly = true)
    @Override
    public List<FamilyMember> getFamilyMembers(Teacher teacher) {
        return familyMemberRepository.findByMainUserIdWithMainUser(teacher.getId());
    }

    @Override
    @Transactional
    public Teacher changePosition(UUID id, TeacherPosition position) {
        if (id == null || position == null) {
            throw new IllegalArgumentException("ID and position cannot be null");
        }
        Optional<Teacher> teacher = findById(id);
        if (teacher.isEmpty()) {
            throw new IllegalArgumentException("Teacher with id " + id + " not found");
        }
        Teacher existingTeacher = teacher.get();
        existingTeacher.setPosition(position);
        return (Teacher) repository.save(existingTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPosition() {
        return baseUserService.findAllByType(Teacher.class).stream()
                .collect(Collectors.groupingBy(Teacher::getPosition, Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySchool() {
        return baseUserService.findAllByType(Teacher.class).stream()
                .collect(Collectors.groupingBy(Teacher::getSchool, Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }
}