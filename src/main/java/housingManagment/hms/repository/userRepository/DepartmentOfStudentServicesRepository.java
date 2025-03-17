package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.DSS;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentOfStudentServicesRepository extends BaseUserRepository<DSS> {
    // Поиск по имени или фамилии (игнорируя регистр)
    List<DSS> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    // Если потребуется поиск по роли (DSS_MANAGER или DSS_ASSISTANT), можно
    // добавить:
    // List<DepartmentOfStudentServices> findByRole(DssRole role);
}
