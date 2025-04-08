package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends BaseUserRepository<Student> {
    @Query("SELECT s FROM Student s WHERE s.role = :role")
    List<Student> findByStudentRole(StudentRole role);

    @Query("SELECT s FROM Student s WHERE s.school = :school")
    List<Student> findBySchool(SchoolsAndSpecialties school);

    @Query("SELECT s FROM Student s WHERE s.specialty = :specialty")
    List<Student> findBySpecialty(@Param("specialty") String specialty);

}
