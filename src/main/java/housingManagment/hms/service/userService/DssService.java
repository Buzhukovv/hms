package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DssService {
    DSS createUser(DSS user);
    DSS updateUser(UUID id, DSS user);
    void deleteUser(UUID id);

    Optional<DSS> findById(UUID id);
    List<DSS> findAll();

    List<DSS> findDSSByRole(DepartmentOfStudentServicesRole role);

    long countByRole();
}