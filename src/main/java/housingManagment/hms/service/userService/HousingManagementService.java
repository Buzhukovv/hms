package housingManagment.hms.service.userService;


import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface HousingManagementService {

    HousingManagement createUser(HousingManagement user);

    HousingManagement updateUser(UUID id, HousingManagement user);

    void deleteUser(UUID id);

    List<HousingManagement> findHousingManagementByRole(HousingManagementRole role);
}
