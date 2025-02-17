package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HousingManagementRepository extends JpaRepository<HousingManagement, UUID> {
    // Поиск по имени или фамилии
    List<HousingManagement> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Поиск по роли (MANAGER или BLOCK_MANAGER)
    List<HousingManagement> findByRole(HousingManagementRole role);
}
