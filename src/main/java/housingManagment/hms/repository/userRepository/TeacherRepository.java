package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    // Поиск по имени или фамилии
    List<Teacher> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Поиск по школе (игнорируя регистр)
    List<Teacher> findBySchoolIgnoreCase(String school);
}
