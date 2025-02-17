package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.Teacher;

import java.util.List;
import java.util.UUID;

public interface TeacherService {
    Teacher createUser(Teacher user);
    Teacher updateUser(UUID id, Teacher user);
    void deleteUser(UUID id);
    Teacher getUserById(UUID id);
    List<Teacher> getAllUsers();
    List<Teacher> searchUsersByNameOrLastName(String keyword);
    List<Teacher> getUsersBySchool(String school);
}
