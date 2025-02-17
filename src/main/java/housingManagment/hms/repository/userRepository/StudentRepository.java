package housingManagment.hms.repository.userRepository;


import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    // Поиск по имени или фамилии
    List<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Поиск по типу студента (MASTER_STUDENT, BACHELOR_DEGREE, DOCTORAL_STUDENT)
    List<Student> findByRole(StudentRole role);
    
    // Поиск по названию школы (игнорируя регистр)
    List<Student> findBySchoolIgnoreCase(String school);
    
    // Поиск по специальности (игнорируя регистр)
    List<Student> findBySpecialtyIgnoreCase(String specialty);
}
