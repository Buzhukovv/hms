package housingManagment.hms.repository;

import housingManagment.hms.entities.ApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, UUID> {
    // Дополнительные запросы можно добавить здесь, если потребуется.
}
