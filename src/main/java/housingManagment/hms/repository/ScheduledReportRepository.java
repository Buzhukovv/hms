package housingManagment.hms.repository;

import housingManagment.hms.entities.ScheduledReport;
import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, UUID> {

    List<ScheduledReport> findByCreatedBy(BaseUser user);

    List<ScheduledReport> findByReportType(String reportType);

    List<ScheduledReport> findByActive(boolean active);

    List<ScheduledReport> findByNextRunAtBefore(LocalDateTime dateTime);

    List<ScheduledReport> findBySchedule(String schedule);

    List<ScheduledReport> findByCreatedByAndActive(BaseUser user, boolean active);
}