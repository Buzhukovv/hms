package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherService {
    Teacher createUser(Teacher user);
    Teacher updateUser(UUID id, Teacher user);
    void deleteUser(UUID id);

    Optional<Teacher> findById(UUID id);
    List<Teacher> findAll();

    List<Teacher> findTeachersByRole(TeacherPosition pos);

    @Transactional(readOnly = true)
    List<Teacher> getUsersBySchool(SchoolsAndSpecialties school);

    @Transactional(readOnly = true)
    List<FamilyMember> getFamilyMembers(Teacher teacher);

    Teacher changePosition(UUID id, TeacherPosition position);

    long countByPosition();
    long countBySchool();
}