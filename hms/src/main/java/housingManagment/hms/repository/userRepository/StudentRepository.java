package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends BaseUserRepository<Student> {
    // Поиск по имени или фамилии
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Поиск по типу студента (MASTER_STUDENT, BACHELOR_DEGREE, DOCTORAL_STUDENT)
    List<Student> findByRole(StudentRole role);

    // Поиск по названию школы
    List<Student> findBySchool(SchoolsAndSpecialties school);

    // Поиск по специальности (игнорируя регистр)
    List<Student> findBySpecialtyIgnoreCase(String specialty);
}
