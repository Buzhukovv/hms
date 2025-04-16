package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends BaseUserRepository {
    // Поиск по школе
    List<Teacher> findBySchool(SchoolsAndSpecialties school);
}
