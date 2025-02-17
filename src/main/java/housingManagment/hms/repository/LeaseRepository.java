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

}
