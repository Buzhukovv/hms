package housingManagment.hms.service.impl;

import housingManagment.hms.dto.FamilyMemberDTO;
import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.FamilyMember;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.propertyRepository.BasePropertyRepository;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final BasePropertyRepository propertyRepository;
    private final BaseUserRepository userRepository;
    private final FamilyMemberRepository familyMemberRepository; // NEW - нужно внедрить

    @Override
    public Lease createLease(LeaseCreateDTO dto) {
        BaseProperty property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        BaseUser tenant = userRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant instanceof Student) {
            if (!(property instanceof DormitoryRoom)) {
                throw new IllegalArgumentException("Student tenant can only be assigned to a DormitoryRoom.");
            }
        } else {
            if (property instanceof DormitoryRoom) {
                throw new IllegalArgumentException("Non-student tenant cannot be assigned to a DormitoryRoom.");
            }
        }

        long activeCount = leaseRepository.countActiveLeasesByPropertyId(property.getId(), LeaseStatus.ACTIVE);
        if (activeCount >= property.getMaxOccupant()) {
            throw new IllegalArgumentException("The property occupancy limit has been reached.");
        }

        Lease lease = new Lease();
        lease.setLeaseNumber(dto.getLeaseNumber());
        lease.setProperty(property);
        lease.setTenant(tenant);
        lease.setStatus(LeaseStatus.valueOf(dto.getStatus()));
        lease.setStartDate(dto.getStartDate());
        lease.setEndDate(dto.getEndDate());
        lease.setCheckInDate(dto.getCheckInDate());
        lease.setCheckOutDate(dto.getCheckOutDate());
        lease.setMonthlyRent(dto.getMonthlyRent());
        lease.setSecurityDeposit(dto.getSecurityDeposit());
        lease.setLeaseTerm(dto.getLeaseTerm());
        lease.setTerms(dto.getTerms());
        lease.setNotes(dto.getNotes());
        lease.setContractNumber(dto.getContractNumber());
        lease.setReservationStatus(dto.getReservationStatus());
        lease.setPenalties(dto.getPenalties());
        lease.setDeposit(dto.getDeposit());

         if ((property instanceof housingManagment.hms.entities.property.CampusApartment
                || property instanceof housingManagment.hms.entities.property.Cottage
                || property instanceof housingManagment.hms.entities.property.OffCampusApartment)
                && dto.getFamilyMembers() != null
                && !dto.getFamilyMembers().isEmpty()) {

            dto.getFamilyMembers().forEach(fmDto -> {
                FamilyMember familyMember = new FamilyMember();
                familyMember.setRelation(fmDto.getRelation());
                familyMember.setFirstName(fmDto.getFirstName());
                familyMember.setLastName(fmDto.getLastName());
                familyMember.setMiddleName(fmDto.getMiddleName());
                familyMember.setEmail(fmDto.getEmail());
                familyMember.setLocalPhone(fmDto.getLocalPhone());
                familyMember.setMainUser(tenant);

                familyMemberRepository.save(familyMember);
            });
        }

        return leaseRepository.save(lease);
    }

    @Override
    public Lease addFamilyMembersToLease(UUID leaseId, List<FamilyMemberDTO> familyMemberDTOs) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + leaseId));

        if (lease.getStatus() != LeaseStatus.ACTIVE && lease.getStatus() != LeaseStatus.RENEWED) {
            throw new IllegalStateException("Cannot add family members to a lease that is not ACTIVE or RENEWED.");
        }

        BaseProperty property = lease.getProperty();
        if (property instanceof DormitoryRoom) {
            throw new IllegalArgumentException("Cannot add family members to a DormitoryRoom lease.");
        }

         long activeCount = leaseRepository.countActiveLeasesByPropertyId(property.getId(), LeaseStatus.ACTIVE);

        if (activeCount + familyMemberDTOs.size() > property.getMaxOccupant()) {
            throw new IllegalArgumentException("Adding these family members would exceed the occupancy limit.");
        }

        BaseUser tenant = lease.getTenant();

        for (FamilyMemberDTO fmDto : familyMemberDTOs) {
            FamilyMember familyMember = new FamilyMember();
            familyMember.setRelation(fmDto.getRelation());
            familyMember.setFirstName(fmDto.getFirstName());
            familyMember.setLastName(fmDto.getLastName());
            familyMember.setMiddleName(fmDto.getMiddleName());
            familyMember.setEmail(fmDto.getEmail());
            familyMember.setLocalPhone(fmDto.getLocalPhone());
            familyMember.setMainUser(tenant);

            familyMemberRepository.save(familyMember);
        }
        return lease;
    }


    @Override
    public Lease terminateLease(UUID id, LocalDate terminationDate) {
        Lease lease = getLeaseById(id);
        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active leases can be terminated");
        }
        lease.setEndDate(terminationDate);
        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setTerminationDate(LocalDate.now());
        return leaseRepository.save(lease);
    }

    @Override
    public Lease updateLease(UUID id, Lease lease) {
        Lease existingLease = getLeaseById(id);
        lease.setId(id);
        return leaseRepository.save(lease);
    }

    @Override
    public void deleteLease(UUID id) {
        Lease lease = getLeaseById(id);
        leaseRepository.delete(lease);
    }

    @Override
    @Transactional(readOnly = true)
    public Lease getLeaseById(UUID id) {
        return leaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getAllLeases() {
        return leaseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getLeasesByProperty(UUID propertyId) {
        return leaseRepository.findByPropertyId(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getLeasesByTenant(UUID tenantId) {
        return leaseRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getLeasesByStatus(LeaseStatus status) {
        return leaseRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getActiveLeases() {
        return leaseRepository.findByStatus(LeaseStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getExpiringLeases() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        return leaseRepository.findLeasesExpiringBetween(today, thirtyDaysFromNow);
    }

    @Override
    public Lease renewLease(UUID id, LocalDate newEndDate) {
        Lease lease = getLeaseById(id);
        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active leases can be renewed");
        }
        lease.setEndDate(newEndDate);
        lease.setStatus(LeaseStatus.RENEWED);
        lease.setRenewalDate(LocalDate.now());
        return leaseRepository.save(lease);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Lease> searchLeases(String keyword) {
        // Implement search functionality based on lease number or other relevant fields
        return leaseRepository.findByLeaseNumberContainingIgnoreCase(keyword);
    }



}