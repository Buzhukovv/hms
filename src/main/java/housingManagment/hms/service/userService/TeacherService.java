package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.TeacherPosition;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    Teacher createUser(Teacher user);
    Teacher updateUser(UUID id, Teacher user);
    void deleteUser(UUID id);

    List<Teacher> findTeachersByRole(TeacherPosition pos);

    @Transactional(readOnly = true)
    List<Teacher> getUsersBySchool(
            SchoolsAndSpecialties school);
}