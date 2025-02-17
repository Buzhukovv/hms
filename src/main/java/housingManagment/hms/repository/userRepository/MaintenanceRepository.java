package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    // Поиск по имени или фамилии
    List<Maintenance> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    // Поиск по роли (MAINTENANCE_MANAGER или MAINTENANCE_STAFF)
    List<Maintenance> findByRole(MaintenanceRole role);
}
