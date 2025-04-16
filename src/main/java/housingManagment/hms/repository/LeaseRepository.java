package housingManagment.hms.repository;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, UUID> {

        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property WHERE l.id = :id")
        Lease findByIdWithDetails(UUID id);

        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property")
        List<Lease> findAllWithDetails();

        List<Lease> findByPropertyId(UUID propertyId);

        @Query("SELECT l FROM Lease l WHERE l.property.id = :propertyId AND l.status = 'ACTIVE' AND l.startDate <= CURRENT_DATE AND (l.endDate IS NULL OR l.endDate >= CURRENT_DATE)")
        List<Lease> findActiveLeasesByPropertyId(UUID propertyId);

        // Fix for DashboardServiceImpl: findByTenant
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property WHERE l.tenant = :tenant")
        List<Lease> findByTenant(@Param("tenant") BaseUser tenant);

        // FindByTenantId
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property WHERE l.tenant.id = :tenantId")
        List<Lease> findByTenantId(@Param("tenantId") UUID tenantId);

        // Fix for LeaseServiceImpl: Add paginated version of findByStatus
        List<Lease> findByStatus(LeaseStatus status);

        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property WHERE l.status = :status")
        List<Lease> findByStatusWithDetails(LeaseStatus status);

        // Add paginated version for findByStatus
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property WHERE l.status = :status")
        Page<Lease> findByStatus(@Param("status") LeaseStatus status, Pageable pageable);

        // Fix for LeaseServiceImpl: Add method with status and search term
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE l.status = :status AND (LOWER(l.leaseNumber) LIKE LOWER(CONCAT('%', :leaseNumber, '%')) " +
                "OR LOWER(l.contractNumber) LIKE LOWER(CONCAT('%', :contractNumber, '%')))")
        Page<Lease> findByStatusAndLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(
                @Param("status") LeaseStatus status,
                @Param("leaseNumber") String leaseNumber,
                @Param("contractNumber") String contractNumber,
                Pageable pageable);

        @Query("SELECT l FROM Lease l WHERE l.status = 'ACTIVE' AND l.endDate BETWEEN :startDate AND :endDate")
        List<Lease> findLeasesExpiringBetween(LocalDate startDate, LocalDate endDate);

        @Query("SELECT l FROM Lease l WHERE (LOWER(l.leaseNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.contractNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<Lease> findByLeaseNumberContainingIgnoreCase(String keyword);

        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE LOWER(l.leaseNumber) LIKE LOWER(CONCAT('%', :leaseNumber, '%')) " +
                "OR LOWER(l.contractNumber) LIKE LOWER(CONCAT('%', :contractNumber, '%'))")
        Page<Lease> findByLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(
                String leaseNumber, String contractNumber, Pageable pageable);

        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE LOWER(l.leaseNumber) LIKE LOWER(CONCAT('%', :leaseNumber, '%')) " +
                "OR LOWER(l.contractNumber) LIKE LOWER(CONCAT('%', :contractNumber, '%'))")
        List<Lease> findByLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(
                String leaseNumber, String contractNumber);

        @Query("SELECT COUNT(l) FROM Lease l WHERE l.property.id = :propertyId AND l.status = :status AND l.startDate <= CURRENT_DATE AND (l.endDate IS NULL OR l.endDate >= CURRENT_DATE)")
        long countActiveLeasesByPropertyId(UUID propertyId, LeaseStatus status);

        // Fix for DashboardServiceImpl: countByStatus
        @Query("SELECT COUNT(l) FROM Lease l WHERE l.status = :status")
        long countByStatus(@Param("status") LeaseStatus status);

        @Query("SELECT l.property, COUNT(l) FROM Lease l WHERE l.status = :status AND l.startDate <= CURRENT_DATE AND (l.endDate IS NULL OR l.endDate >= CURRENT_DATE) GROUP BY l.property")
        List<Object[]> countActiveLeasesByProperty(LeaseStatus status);

        // Fix for DashboardServiceImpl: findHistoricalLeasesForStudent
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE l.tenant = :student AND l.status IN ('TERMINATED', 'EXPIRED')")
        List<Lease> findHistoricalLeasesForStudent(@Param("student") BaseUser student);

        // Fix for DashboardServiceImpl: findActiveLeaseForTeacher
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE l.tenant = :teacher AND l.status = 'ACTIVE' AND l.startDate <= CURRENT_DATE " +
                "AND (l.endDate IS NULL OR l.endDate >= CURRENT_DATE)")
        Lease findActiveLeaseForTeacher(@Param("teacher") BaseUser teacher);

        // Fix for DashboardServiceImpl: findHistoricalLeasesForTeacher
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property " +
                "WHERE l.tenant = :teacher AND l.status IN ('TERMINATED', 'EXPIRED')")
        List<Lease> findHistoricalLeasesForTeacher(@Param("teacher") BaseUser teacher);

        // Fix for DashboardServiceImpl: findByPropertyBlock
        @Query("SELECT l FROM Lease l LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.property p " +
                "WHERE p.propertyBlock = :block")
        List<Lease> findByPropertyBlock(@Param("block") String propertyBlock);

}