package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentService {
    Student createUser(Student user);
    Student updateUser(UUID id, Student user);
    void deleteUser(UUID id);

    Optional<Student> findById(UUID id);
    List<Student> findAll();

    @Transactional(readOnly = true)
    List<Student> getUsersBySchool(SchoolsAndSpecialties school);

    List<Student> getUsersBySpecialty(String specialty);
    List<Student> findStudentsByRole(StudentRole role);

    List<Student> getRoommates(UUID id);
    List<Student> getExRoommates(UUID id);

    Student changeRole(UUID id, StudentRole role);
    Student changeSchool(UUID id, SchoolsAndSpecialties school);

    long countByRole();
    long countBySchool();
    long countBySpecialty();
}