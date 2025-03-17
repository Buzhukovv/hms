package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Teacher;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends BaseUserRepository<Teacher> {
    // Поиск по имени или фамилии
    List<Teacher> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Поиск по школе (игнорируя регистр)
    List<Teacher> findBySchoolIgnoreCase(String school);
}
