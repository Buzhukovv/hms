package housingManagment.hms.service;

import housingManagment.hms.dto.FamilyMemberDTO;
import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeaseService {

    Lease createLease(LeaseCreateDTO dto);

    Lease updateLease(UUID id, Lease lease);

    void deleteLease(UUID id);

    Lease getLeaseById(UUID id);

    List<Lease> getAllLeases();

    List<Lease> getLeasesByProperty(UUID propertyId);

    List<Lease> getActiveLeasesByProperty(UUID propertyId);

    List<Lease> getLeasesByTenant(UUID tenantId);

    List<Lease> getLeasesByStatus(LeaseStatus status);

    List<Lease> getActiveLeases();

    List<Lease> getExpiringLeases();

    Lease renewLease(UUID id, LocalDate newEndDate);

    Lease terminateLease(UUID id, LocalDate terminationDate);

    List<Lease> searchLeases(String keyword);

    Lease addFamilyMembersToLease(UUID leaseId, List<FamilyMemberDTO> familyMemberDTOs);

    /**
     * Get paginated leases with optional filtering by status and search term.
     * Eager-loads tenant and property data to prevent LazyInitializationException.
     *
     * @param pageable   Pagination information
     * @param status     Filter by lease status (optional)
     * @param searchTerm Search in lease number or contract number (optional)
     * @return Page of Lease objects with initialized associations
     */
    Page<Lease> getLeasesPaginated(Pageable pageable, LeaseStatus status, String searchTerm);
}
