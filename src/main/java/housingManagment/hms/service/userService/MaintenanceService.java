package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.Maintenance;

import java.util.List;
import java.util.UUID;

public interface MaintenanceService {
    Maintenance createUser(Maintenance user);
    Maintenance updateUser(UUID id, Maintenance user);
    void deleteUser(UUID id);
    Maintenance getUserById(UUID id);
    List<Maintenance> getAllUsers();
    List<Maintenance> searchUsersByNameOrLastName(String keyword);
    List<Maintenance> getUsersByRole(String role); // role: MAINTENANCE_MANAGER, MAINTENANCE_STAFF
}
