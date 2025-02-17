package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.DSS;

import java.util.List;
import java.util.UUID;

public interface DssService {
    DSS createUser(DSS user);
    DSS updateUser(UUID id, DSS user);
    void deleteUser(UUID id);
    DSS getUserById(UUID id);
    List<DSS> getAllUsers();
    List<DSS> searchUsersByNameOrLastName(String keyword);
    List<DSS> getUsersByRole(String role); // role: DSS_MANAGER, DSS_ASSISTANT
}
