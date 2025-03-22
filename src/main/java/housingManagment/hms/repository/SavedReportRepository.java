package housingManagment.hms.repository;

import housingManagment.hms.entities.SavedReport;
import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavedReportRepository extends JpaRepository<SavedReport, UUID> {

    List<SavedReport> findByCreatedBy(BaseUser user);

    List<SavedReport> findByReportType(String reportType);

    List<SavedReport> findByCreatedByAndReportType(BaseUser user, String reportType);

    List<SavedReport> findByNameContainingIgnoreCase(String keyword);
}