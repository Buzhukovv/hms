package housingManagment.hms.service;

import housingManagment.hms.dto.FamilyMemberDTO;
import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.enums.LeaseStatus;

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

}
