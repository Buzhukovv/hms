package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.Student;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    Student createUser(Student user);
    Student updateUser(UUID id, Student user);
    void deleteUser(UUID id);
    Student getUserById(UUID id);
    List<Student> getAllUsers();
    List<Student> searchUsersByNameOrLastName(String keyword);
    List<Student> getUsersByRole(String role); // role: MASTER_STUDENT, BACHELOR_DEGREE, DOCTORAL_STUDENT
    List<Student> getUsersBySchool(String school);
    List<Student> getUsersBySpecialty(String specialty);
}
