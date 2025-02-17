package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.HousingManagement;

import java.util.List;
import java.util.UUID;

public interface HousingManagementService {
    HousingManagement createUser(HousingManagement user);
    HousingManagement updateUser(UUID id, HousingManagement user);
    void deleteUser(UUID id);
    HousingManagement getUserById(UUID id);
    List<HousingManagement> getAllUsers();
    List<HousingManagement> searchUsersByNameOrLastName(String keyword);
    List<HousingManagement> getUsersByRole(String role); // role: MANAGER, BLOCK_MANAGER
}
