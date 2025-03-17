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
         * Calculate average resolution time in days for requests completed in a period
         */
        @Query(value = "SELECT COALESCE(AVG(EXTRACT(EPOCH FROM (completed_at - created_at)) / 86400.0), 0) " +
                        "FROM maintenance_requests " +
                        "WHERE completed_at BETWEEN :startDate AND :endDate " +
                        "AND status = 'COMPLETED'", nativeQuery = true)
        double calculateAverageResolutionTime(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

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

        /**
         * Calculate total cost for maintenance requests in a period
         */
        @Query(value = "SELECT COALESCE(SUM(cost), 0) " +
                        "FROM maintenance_requests " +
                        "WHERE created_at BETWEEN :startDate AND :endDate " +
                        "AND status = 'COMPLETED'", nativeQuery = true)
        double calculateTotalCostBetween(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Get maintenance cost by request type
         */
        @Query(value = "SELECT request_type as requestType, SUM(cost) as cost " +
                        "FROM maintenance_requests " +
                        "WHERE created_at BETWEEN :startDate AND :endDate " +
                        "AND status = 'COMPLETED' " +
                        "GROUP BY request_type", nativeQuery = true)
        Map<String, Object> getCostByRequestType(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Get performance metrics for specified maintenance staff
         */
        @Query(value = "SELECT " +
                        "assigned_to as staffId, " +
                        "CONCAT(u.first_name, ' ', u.last_name) as name, " +
                        "COUNT(m.id) as completedRequests, " +
                        "COALESCE(AVG(EXTRACT(EPOCH FROM (m.completed_at - m.created_at)) / 86400.0), 0) as averageResolutionTime "
                        +
                        "FROM maintenance_requests m " +
                        "JOIN user_base u ON m.assigned_to = u.id " +
                        "WHERE m.status = 'COMPLETED' " +
                        "AND m.completed_at BETWEEN :startDate AND :endDate " +
                        "AND m.assigned_to IN :staffIds " +
                        "GROUP BY m.assigned_to, u.first_name, u.last_name", nativeQuery = true)
        List<Map<String, Object>> getStaffPerformanceByIds(@Param("staffIds") List<UUID> staffIds,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        /**
         * Get performance metrics for all maintenance staff
         */
        @Query(value = "SELECT " +
                        "assigned_to as staffId, " +
                        "CONCAT(u.first_name, ' ', u.last_name) as name, " +
                        "COUNT(m.id) as completedRequests, " +
                        "COALESCE(AVG(EXTRACT(EPOCH FROM (m.completed_at - m.created_at)) / 86400.0), 0) as averageResolutionTime "
                        +
                        "FROM maintenance_requests m " +
                        "JOIN user_base u ON m.assigned_to = u.id " +
                        "WHERE m.status = 'COMPLETED' " +
                        "AND m.completed_at BETWEEN :startDate AND :endDate " +
                        "GROUP BY m.assigned_to, u.first_name, u.last_name", nativeQuery = true)
        List<Map<String, Object>> getAllStaffPerformance(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}