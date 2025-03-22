package housingManagment.hms.repository;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, UUID> {

        List<Lease> findByTenant(BaseUser tenant);

        List<Lease> findByProperty(BaseProperty property);

        List<Lease> findByStatus(LeaseStatus status);

        List<Lease> findByEndDateBefore(LocalDate date);

        Optional<Lease> findByLeaseNumber(String leaseNumber);

        List<Lease> findByTenantAndStatus(BaseUser tenant, LeaseStatus status);

        Optional<Lease> findByContractNumber(String contractNumber);

        List<Lease> findByTenantId(UUID tenantId);

        List<Lease> findByPropertyId(UUID propertyId);

        List<Lease> findByLeaseNumberContainingIgnoreCase(String keyword);

        @Query("SELECT l FROM Lease l WHERE l.checkOutDate >= :date AND l.status = 'ACTIVE'")
        List<Lease> findActiveLeasesByDate(@Param("date") LocalDate date);

        @Query("SELECT l FROM Lease l WHERE l.tenant.id = :tenantId AND l.status = 'ACTIVE'")
        List<Lease> findActiveLeasesByTenantId(@Param("tenantId") UUID tenantId);

        @Query("SELECT l FROM Lease l WHERE l.property.id = :propertyId AND l.status = 'ACTIVE'")
        List<Lease> findActiveLeasesByPropertyId(@Param("propertyId") UUID propertyId);

        @Query("SELECT COUNT(l) > 0 FROM Lease l WHERE l.property.id = :propertyId " +
                        "AND l.status = 'ACTIVE' AND :checkInDate < l.checkOutDate AND :checkOutDate > l.checkInDate")
        boolean isPropertyAvailableForPeriod(@Param("propertyId") UUID propertyId,
                        @Param("checkInDate") LocalDate checkInDate,
                        @Param("checkOutDate") LocalDate checkOutDate);

        @Query("SELECT l FROM Lease l WHERE l.endDate BETWEEN :startDate AND :endDate")
        List<Lease> findLeasesExpiringBetween(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT l FROM Lease l WHERE l.endDate < :date")
        List<Lease> findExpiredLeases(@Param("date") LocalDate date);

        @Query("SELECT l FROM Lease l WHERE l.status = 'ACTIVE'")
        List<Lease> findActiveLeases();

        @Query("SELECT COUNT(l) FROM Lease l WHERE l.property.id = :propertyId AND l.status = :activeStatus")
        long countActiveLeasesByPropertyId(@Param("propertyId") UUID propertyId,
                        @Param("activeStatus") LeaseStatus activeStatus);

        /**
         * Count leases that were active during a given period
         */
        @Query("SELECT COUNT(l) FROM Lease l WHERE l.startDate <= :endDate AND l.endDate >= :startDate")
        long countByStartDateBeforeAndEndDateAfter(@Param("endDate") LocalDate endDate,
                        @Param("startDate") LocalDate startDate);

        /**
         * Calculate average vacancy duration in days
         */
        @Query(value = "SELECT COALESCE(AVG(EXTRACT(DAY FROM (next_lease.start_date - prev_lease.end_date))), 0) " +
                        "FROM lease prev_lease " +
                        "JOIN lease next_lease ON prev_lease.property_id = next_lease.property_id " +
                        "WHERE prev_lease.end_date BETWEEN :startDate AND :endDate " +
                        "AND next_lease.start_date > prev_lease.end_date", nativeQuery = true)
        double findAverageVacancyDuration(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        /**
         * Count active leases for a specific property type in a given period
         */
        @Query("SELECT COUNT(l) FROM Lease l JOIN l.property p " +
                        "WHERE TYPE(p) = :propertyType " +
                        "AND l.startDate <= :endDate AND l.endDate >= :startDate")
        long countByPropertyTypeAndDateRange(
                        @Param("propertyType") Class<? extends BaseProperty> propertyType,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Find occupancy trend data for the last N months
         */
        @Query(value = "SELECT " +
                        "TO_CHAR(d.month_date, 'Month') as month, " +
                        "COALESCE(COUNT(l.id) * 100.0 / (SELECT COUNT(p.id) FROM property_base p), 0) as occupancy_rate "
                        +
                        "FROM " +
                        "generate_series(:startDate, :endDate, interval '1 month') AS d(month_date) " +
                        "LEFT JOIN leases l ON " +
                        "l.start_date <= d.month_date AND l.end_date >= d.month_date " +
                        "GROUP BY d.month_date " +
                        "ORDER BY d.month_date", nativeQuery = true)
        List<Map<String, Object>> findOccupancyTrend(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calculate total security deposits held
         */
        @Query("SELECT SUM(l.securityDeposit) FROM Lease l WHERE l.endDate >= CURRENT_DATE")
        double calculateTotalSecurityDeposits();

        /**
         * Count active tenants in a period
         */
        @Query("SELECT COUNT(DISTINCT l.tenant.id) FROM Lease l " +
                        "WHERE l.startDate <= :endDate AND l.endDate >= :startDate")
        long countActiveTenantsInPeriod(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Count new tenants in a period
         */
        @Query("SELECT COUNT(DISTINCT l.tenant.id) FROM Lease l " +
                        "WHERE l.startDate BETWEEN :startDate AND :endDate")
        long countNewTenantsInPeriod(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Count departing tenants in a period
         */
        @Query("SELECT COUNT(DISTINCT l.tenant.id) FROM Lease l " +
                        "WHERE l.endDate BETWEEN :startDate AND :endDate")
        long countDepartingTenantsInPeriod(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Calculate average lease duration in months
         */
        @Query(value = "SELECT COALESCE(AVG(EXTRACT(MONTH FROM (end_date - start_date))), 0) FROM leases", nativeQuery = true)
        double calculateAverageLeaseDuration();

        /**
         * Get lease duration distribution
         */
        @Query(value = "SELECT " +
                        "SUM(CASE WHEN duration < 6 THEN 1 ELSE 0 END) as \"lessThan6Months\", " +
                        "SUM(CASE WHEN duration >= 6 AND duration < 12 THEN 1 ELSE 0 END) as \"6To12Months\", " +
                        "SUM(CASE WHEN duration >= 12 AND duration < 24 THEN 1 ELSE 0 END) as \"12To24Months\", " +
                        "SUM(CASE WHEN duration >= 24 THEN 1 ELSE 0 END) as \"moreThan24Months\" " +
                        "FROM (" +
                        "   SELECT EXTRACT(MONTH FROM (end_date - start_date)) as duration " +
                        "   FROM leases" +
                        ") lease_duration", nativeQuery = true)
        Map<String, Object> getLeaseDurationDistribution();

        /**
         * Get tenant turnover by month
         */
        @Query(value = "SELECT " +
                        "TO_CHAR(d.month_date, 'Month') as month, " +
                        "COALESCE(new_tenants.count, 0) as new_tenants, " +
                        "COALESCE(departing_tenants.count, 0) as departing_tenants " +
                        "FROM " +
                        "generate_series(:startDate, :endDate, interval '1 month') AS d(month_date) " +
                        "LEFT JOIN (" +
                        "   SELECT DATE_TRUNC('month', start_date) as month, COUNT(DISTINCT tenant_id) as count " +
                        "   FROM leases " +
                        "   WHERE start_date BETWEEN :startDate AND :endDate " +
                        "   GROUP BY DATE_TRUNC('month', start_date)" +
                        ") new_tenants ON DATE_TRUNC('month', d.month_date) = new_tenants.month " +
                        "LEFT JOIN (" +
                        "   SELECT DATE_TRUNC('month', end_date) as month, COUNT(DISTINCT tenant_id) as count " +
                        "   FROM leases " +
                        "   WHERE end_date BETWEEN :startDate AND :endDate " +
                        "   GROUP BY DATE_TRUNC('month', end_date)" +
                        ") departing_tenants ON DATE_TRUNC('month', d.month_date) = departing_tenants.month " +
                        "ORDER BY d.month_date", nativeQuery = true)
        List<Map<String, Object>> getTenantTurnoverByMonth(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Get tenant count by locations
         */
        @Query("SELECT p.propertyBlock as location, COUNT(DISTINCT l.tenant.id) as count " +
                        "FROM Lease l JOIN l.property p " +
                        "WHERE l.startDate <= :endDate AND l.endDate >= :startDate " +
                        "AND p.propertyBlock IN :locations " +
                        "GROUP BY p.propertyBlock")
        Map<String, Object> getTenantCountByLocations(
                        @Param("locations") List<String> locations,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // Custom query to eagerly fetch tenant and property for a specific lease
        @Query("SELECT l FROM Lease l JOIN FETCH l.tenant JOIN FETCH l.property WHERE l.id = :id")
        Optional<Lease> findByIdWithDetails(@Param("id") UUID id);

        // Custom query to eagerly fetch tenant and property for all leases
        @Query("SELECT l FROM Lease l JOIN FETCH l.tenant JOIN FETCH l.property")
        List<Lease> findAllWithDetails();

        // Custom query to eagerly fetch tenant and property for leases by status
        @Query("SELECT l FROM Lease l JOIN FETCH l.tenant JOIN FETCH l.property WHERE l.status = :status")
        List<Lease> findByStatusWithDetails(@Param("status") LeaseStatus status);

        @Query("SELECT l.property, COUNT(l) as leaseCount FROM Lease l WHERE l.status = :status GROUP BY l.property")
        List<Object[]> countActiveLeasesByProperty(@Param("status") LeaseStatus status);

        @Query("SELECT DISTINCT l.property FROM Lease l WHERE l.property.id = :propertyId")
        Optional<BaseProperty> findPropertyByLeasePropertyId(@Param("propertyId") UUID propertyId);
}
