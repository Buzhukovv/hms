package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HousingManagementService {
    HousingManagement createUser(HousingManagement user);
    HousingManagement updateUser(UUID id, HousingManagement user);
    void deleteUser(UUID id);

    Optional<HousingManagement> findById(UUID id);
    List<HousingManagement> findAll();

    List<HousingManagement> findHousingManagementByRole(HousingManagementRole role);
    List<HousingManagement> findByBlock(String block);

    void assignBlock(UUID id, String block);
    long countByRole(HousingManagementRole role);

    long countAllMember();
    long countByBlock(String block);
}