package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;

import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
    Maintenance createUser(Maintenance user);
    Maintenance updateUser(UUID id, Maintenance user);
    void deleteUser(UUID id);
    List<Maintenance> findMaintenanceByRole(MaintenanceRole role);
}
