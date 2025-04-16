package housingManagment.hms.repository;

import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {

        Optional<MaintenanceRequest> findByRequestNumber(String requestNumber);

        List<MaintenanceRequest> findByRequesterId(UUID requesterId);

        List<MaintenanceRequest> findByLeaseId(UUID leaseId);

        List<MaintenanceRequest> findByAssignedTo(Maintenance maintenance);

        List<MaintenanceRequest> findByStatus(MaintenanceRequestStatus status);

        List<MaintenanceRequest> findByIsPaid(Boolean isPaid);

        List<MaintenanceRequest> findByRequester(BaseUser requester);

        /**
         * Count requests created between two dates
         */
        long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        /**
         * Count requests with specific statuses created between two dates
         */
        long countByStatusInAndCreatedAtBetween(List<String> statuses, LocalDateTime startDate, LocalDateTime endDate);

        /**
         * Count requests with a specific status that were completed between two dates
         */
        long countByStatusAndCompletedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate);

        /**
         * Count requests with a specific status created between two dates
         */
        long countByStatusAndCreatedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate);

        /**
         * Get request count by status
         */
        @Query(value = "SELECT status as status, COUNT(*) as count " +
                "FROM maintenance_requests " +
                "WHERE created_at BETWEEN :startDate AND :endDate " +
                "GROUP BY status", nativeQuery = true)
        Map<String, Object> getRequestStatusCounts(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

        /**
         * Get request count by type
         */
        @Query(value = "SELECT request_type as requestType, COUNT(*) as count " +
                "FROM maintenance_requests " +
                "WHERE created_at BETWEEN :startDate AND :endDate " +
                "GROUP BY request_type", nativeQuery = true)
        Map<String, Object> getRequestTypeCounts(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);


        @Query("SELECT mr FROM MaintenanceRequest mr " +
                "JOIN mr.lease l " +
                "JOIN l.property p " +
                "WHERE p.propertyBlock = :block")
        List<MaintenanceRequest> findByPropertyBlock(@Param("block") String block);


}