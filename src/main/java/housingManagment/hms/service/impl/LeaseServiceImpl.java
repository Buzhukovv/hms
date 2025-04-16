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
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.userRepository.FamilyMemberRepository;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaseServiceImpl implements LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final BaseUserRepository baseUserRepository;
    private final FamilyMemberRepository familyMemberRepository;

    /**
     * Update property status based on current active leases.
     * This method should be called after any lease operation that might affect
     * property occupancy.
     *
     * @param propertyId The ID of the property to update
     */
    private void updatePropertyStatusBasedOnLeases(UUID propertyId) {
        try {
            BaseProperty property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

            // Skip update for properties with special statuses
            if (property.getStatus() == PropertyStatus.RESERVED
                    || property.getStatus() == PropertyStatus.OUT_OF_SERVICE) {
                return;
            }

            // Count active leases for this property
            long activeLeaseCount = leaseRepository.countActiveLeasesByPropertyId(propertyId, LeaseStatus.ACTIVE);

            // Update property status based on active lease count
            if (activeLeaseCount <= 0) {
                property.setStatus(PropertyStatus.VACANT);
            } else if (activeLeaseCount >= property.getMaxOccupant()) {
                property.setStatus(PropertyStatus.OCCUPIED);
            } else {
                property.setStatus(PropertyStatus.PARTIALLY_OCCUPIED);
            }

            propertyRepository.save(property);
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Error updating property status: " + e.getMessage());
        }
    }

    @Override
    public Lease createLease(LeaseCreateDTO dto) {
        // Validate input
        if (dto == null) {
            throw new IllegalArgumentException("LeaseCreateDTO cannot be null");
        }
        if (dto.getPropertyId() == null || dto.getTenantId() == null) {
            throw new IllegalArgumentException("Property ID and Tenant ID cannot be null");
        }
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("Lease status cannot be null");
        }

        // Find the property
        BaseProperty property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + dto.getPropertyId()));

        // Find the tenant
        BaseUser tenant = baseUserRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + dto.getTenantId()));

        // Validate tenant and property compatibility
        if (tenant instanceof Student) {
            if (!(property instanceof DormitoryRoom)) {
                throw new IllegalArgumentException("Student tenant can only be assigned to a DormitoryRoom.");
            }
        } else {
            if (property instanceof DormitoryRoom) {
                throw new IllegalArgumentException("Non-student tenant cannot be assigned to a DormitoryRoom.");
            }
        }

        // Check occupancy limit
        long activeCount = leaseRepository.countActiveLeasesByPropertyId(property.getId(), LeaseStatus.ACTIVE);
        if (activeCount >= property.getMaxOccupant()) {
            throw new IllegalArgumentException("The property occupancy limit has been reached.");
        }

        // Create the lease
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

        // Generate a random lease number if not provided
        if (lease.getLeaseNumber() == null || lease.getLeaseNumber().trim().isEmpty()) {
            String dateStr = LocalDate.now().toString().replace("-", "");
            String random = String.format("%04d", (int) (Math.random() * 10000));
            lease.setLeaseNumber("LSE-" + dateStr + "-" + random);
        }

        // Generate a random contract number if not provided
        if (lease.getContractNumber() == null || lease.getContractNumber().trim().isEmpty()) {
            String dateStr = LocalDate.now().toString().replace("-", "");
            String random = String.format("%04d", (int) (Math.random() * 10000));
            lease.setContractNumber("CNT-" + dateStr + "-" + random);
        }

        // Save the lease
        Lease savedLease = leaseRepository.save(lease);

        // Update property status if the lease is active
        if (lease.getStatus() == LeaseStatus.ACTIVE) {
            updatePropertyStatusBasedOnLeases(property.getId());
        }

        // Add family members if provided
        if (dto.getFamilyMembers() != null && !dto.getFamilyMembers().isEmpty()) {
            if (property instanceof DormitoryRoom) {
                throw new IllegalArgumentException("Cannot add family members to a DormitoryRoom lease.");
            }
            if (activeCount + dto.getFamilyMembers().size() > property.getMaxOccupant()) {
                throw new IllegalArgumentException("Adding these family members would exceed the occupancy limit.");
            }
            for (FamilyMemberDTO familyMemberDTO : dto.getFamilyMembers()) {
                FamilyMember familyMember = new FamilyMember();
                familyMember.setFirstName(familyMemberDTO.getFirstName());
                familyMember.setLastName(familyMemberDTO.getLastName());
                familyMember.setMiddleName(familyMemberDTO.getMiddleName());
                familyMember.setEmail(familyMemberDTO.getEmail());
                familyMember.setRelation(familyMemberDTO.getRelation());
                familyMember.setLocalPhone(familyMemberDTO.getLocalPhone());
                familyMember.setMainUser(tenant);
                familyMemberRepository.save(familyMember);
            }
        }

        return savedLease;
    }

    @Override
    public Lease updateLease(UUID id, Lease updatedLease) {
        if (id == null || updatedLease == null) {
            throw new IllegalArgumentException("ID and lease cannot be null");
        }

        // Find existing lease
        Lease existingLease = getLeaseById(id);
        LeaseStatus previousStatus = existingLease.getStatus();
        BaseProperty property = existingLease.getProperty();
        UUID propertyId = property.getId();
        boolean statusChanged = false;

        // Selectively update fields that are not null in the updated lease
        if (updatedLease.getLeaseNumber() != null && !updatedLease.getLeaseNumber().isEmpty()) {
            existingLease.setLeaseNumber(updatedLease.getLeaseNumber());
        }
        if (updatedLease.getStatus() != null) {
            if (previousStatus != updatedLease.getStatus()) {
                statusChanged = true;
            }
            existingLease.setStatus(updatedLease.getStatus());
        }
        if (updatedLease.getStartDate() != null) {
            existingLease.setStartDate(updatedLease.getStartDate());
        }
        if (updatedLease.getEndDate() != null) {
            existingLease.setEndDate(updatedLease.getEndDate());
        }
        if (updatedLease.getCheckInDate() != null) {
            existingLease.setCheckInDate(updatedLease.getCheckInDate());
        }
        if (updatedLease.getCheckOutDate() != null) {
            existingLease.setCheckOutDate(updatedLease.getCheckOutDate());
        }
        if (updatedLease.getMonthlyRent() != null) {
            existingLease.setMonthlyRent(updatedLease.getMonthlyRent());
        }
        if (updatedLease.getSecurityDeposit() != null) {
            existingLease.setSecurityDeposit(updatedLease.getSecurityDeposit());
        }
        if (updatedLease.getLeaseTerm() != null) {
            existingLease.setLeaseTerm(updatedLease.getLeaseTerm());
        }
        if (updatedLease.getTerms() != null) {
            existingLease.setTerms(updatedLease.getTerms());
        }
        if (updatedLease.getNotes() != null) {
            existingLease.setNotes(updatedLease.getNotes());
        }
        if (updatedLease.getContractNumber() != null && !updatedLease.getContractNumber().isEmpty()) {
            existingLease.setContractNumber(updatedLease.getContractNumber());
        }
        if (updatedLease.getReservationStatus() != null) {
            existingLease.setReservationStatus(updatedLease.getReservationStatus());
        }
        if (updatedLease.getPenalties() != null) {
            existingLease.setPenalties(updatedLease.getPenalties());
        }
        if (updatedLease.getDeposit() != null) {
            existingLease.setDeposit(updatedLease.getDeposit());
        }
        if (updatedLease.getTerminationDate() != null) {
            existingLease.setTerminationDate(updatedLease.getTerminationDate());
        }

        // Update the update timestamp
        existingLease.setUpdatedAt(LocalDateTime.now());

        // Save the updated lease
        Lease savedLease = leaseRepository.save(existingLease);

        // If the status was changed to or from ACTIVE, update property status
        if (statusChanged && (previousStatus == LeaseStatus.ACTIVE || existingLease.getStatus() == LeaseStatus.ACTIVE)) {
            updatePropertyStatusBasedOnLeases(propertyId);
        }

        return savedLease;
    }

    @Override
    public void deleteLease(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Lease lease = getLeaseById(id);
        UUID propertyId = lease.getProperty().getId();
        boolean wasActive = (lease.getStatus() == LeaseStatus.ACTIVE);

        // Delete the lease
        leaseRepository.delete(lease);

        // Update property status if the lease was active
        if (wasActive) {
            updatePropertyStatusBasedOnLeases(propertyId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Lease getLeaseById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Lease> leaseOptional = Optional.ofNullable(leaseRepository.findByIdWithDetails(id));
        return leaseOptional.orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getAllLeases() {
        List<Lease> leases;
        try {
            leases = leaseRepository.findAllWithDetails();
        } catch (Exception e) {
            // Fallback to regular findAll if the join fetch query fails
            leases = leaseRepository.findAll();
        }

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "leaseQueries", key = "'property_' + #propertyId")
    public List<Lease> getLeasesByProperty(UUID propertyId) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }

        List<Lease> leases = leaseRepository.findByPropertyId(propertyId);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "leaseQueries", key = "'active_property_' + #propertyId")
    public List<Lease> getActiveLeasesByProperty(UUID propertyId) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }

        List<Lease> leases = leaseRepository.findActiveLeasesByPropertyId(propertyId);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getLeasesByTenant(UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }

        BaseUser tenant = baseUserRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with ID: " + tenantId));
        List<Lease> leases = leaseRepository.findByTenant(tenant);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getLeasesByStatus(LeaseStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Lease status cannot be null");
        }

        List<Lease> leases;
        try {
            leases = leaseRepository.findByStatusWithDetails(status);
        } catch (Exception e) {
            // Fallback to regular findByStatus if the join fetch query fails
            leases = leaseRepository.findByStatus(status);
        }

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getActiveLeases() {
        List<Lease> leases = leaseRepository.findByStatus(LeaseStatus.ACTIVE);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> getExpiringLeases() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);
        List<Lease> leases = leaseRepository.findLeasesExpiringBetween(today, thirtyDaysFromNow);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    public Lease renewLease(UUID id, LocalDate newEndDate) {
        if (id == null || newEndDate == null) {
            throw new IllegalArgumentException("ID and new end date cannot be null");
        }

        Lease lease = getLeaseById(id);
        UUID propertyId = lease.getProperty().getId();
        boolean wasActive = (lease.getStatus() == LeaseStatus.ACTIVE);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active leases can be renewed");
        }
        if (newEndDate.isBefore(lease.getEndDate())) {
            throw new IllegalArgumentException("New end date must be after the current end date");
        }

        lease.setEndDate(newEndDate);
        lease.setStatus(LeaseStatus.RENEWED);
        lease.setRenewalDate(LocalDate.now());

        Lease savedLease = leaseRepository.save(lease);

        // Update property status if status changed from ACTIVE
        if (wasActive) {
            updatePropertyStatusBasedOnLeases(propertyId);
        }

        return savedLease;
    }

    @Override
    public Lease terminateLease(UUID id, LocalDate terminationDate) {
        if (id == null || terminationDate == null) {
            throw new IllegalArgumentException("ID and termination date cannot be null");
        }

        Lease lease = getLeaseById(id);
        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new IllegalStateException("Only active leases can be terminated");
        }
        if (terminationDate.isBefore(lease.getStartDate())) {
            throw new IllegalArgumentException("Termination date cannot be before the start date");
        }

        BaseProperty property = lease.getProperty();
        UUID propertyId = property.getId();

        lease.setEndDate(terminationDate);
        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setTerminationDate(LocalDate.now());

        // Save the lease first
        Lease savedLease = leaseRepository.save(lease);

        // Update property status after terminating the lease
        updatePropertyStatusBasedOnLeases(propertyId);

        return savedLease;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lease> searchLeases(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }

        // Use the non-paginated version since searchLeases returns a List
        List<Lease> leases = leaseRepository.findByLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(keyword, keyword);

        // Initialize tenant and property for each lease
        for (Lease lease : leases) {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        }

        return leases;
    }

    @Override
    public Lease addFamilyMembersToLease(UUID leaseId, List<FamilyMemberDTO> familyMemberDTOs) {
        if (leaseId == null || familyMemberDTOs == null || familyMemberDTOs.isEmpty()) {
            throw new IllegalArgumentException("Lease ID and family member DTOs cannot be null or empty");
        }

        Optional<Lease> leaseOptional = leaseRepository.findById(leaseId);
        Lease lease = leaseOptional.orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + leaseId));

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
            if (fmDto.getFirstName() == null || fmDto.getLastName() == null || fmDto.getRelation() == null) {
                throw new IllegalArgumentException("Family member first name, last name, and relation cannot be null");
            }
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
    @Transactional(readOnly = true)
    public Page<Lease> getLeasesPaginated(Pageable pageable, LeaseStatus status, String searchTerm) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        Page<Lease> leases;

        if (status != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Filter by both status and search term
            leases = leaseRepository.findByStatusAndLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(
                    status, searchTerm, searchTerm, pageable);
        } else if (status != null) {
            // Filter by status only
            leases = leaseRepository.findByStatus(status, pageable);
        } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Filter by search term only
            leases = leaseRepository.findByLeaseNumberContainingIgnoreCaseOrContractNumberContainingIgnoreCase(
                    searchTerm, searchTerm, pageable);
        } else {
            // No filters
            leases = leaseRepository.findAll(pageable);
        }

        // Initialize associations to prevent LazyInitializationException
        leases.forEach(lease -> {
            if (lease.getTenant() != null) {
                Hibernate.initialize(lease.getTenant());
            }
            if (lease.getProperty() != null) {
                Hibernate.initialize(lease.getProperty());
            }
        });

        return leases;
    }

    /**
     * Synchronize all property statuses based on active leases.
     * This is useful for batch updates or repairs if statuses get out of sync.
     */
    @Transactional
    public void synchronizeAllPropertyStatuses() {
        try {
            // Get all properties with active lease counts
            List<Object[]> propertyLeaseCountPairs = leaseRepository.countActiveLeasesByProperty(LeaseStatus.ACTIVE);

            for (Object[] pair : propertyLeaseCountPairs) {
                if (pair.length >= 2) {
                    BaseProperty property = (BaseProperty) pair[0];
                    Long activeLeaseCount = ((Number) pair[1]).longValue();

                    // Skip properties with special statuses
                    if (property.getStatus() == PropertyStatus.RESERVED ||
                            property.getStatus() == PropertyStatus.OUT_OF_SERVICE) {
                        continue;
                    }

                    // Update property status based on active lease count
                    if (activeLeaseCount <= 0) {
                        property.setStatus(PropertyStatus.VACANT);
                    } else if (activeLeaseCount >= property.getMaxOccupant()) {
                        property.setStatus(PropertyStatus.OCCUPIED);
                    } else {
                        property.setStatus(PropertyStatus.PARTIALLY_OCCUPIED);
                    }

                    propertyRepository.save(property);
                }
            }

            // Find properties with no active leases and set them to VACANT
            List<BaseProperty> allProperties = propertyRepository.findAll();
            List<UUID> propertiesWithLeases = propertyLeaseCountPairs.stream()
                    .map(pair -> ((BaseProperty) pair[0]).getId())
                    .toList();

            for (BaseProperty property : allProperties) {
                if (!propertiesWithLeases.contains(property.getId()) &&
                        property.getStatus() != PropertyStatus.RESERVED &&
                        property.getStatus() != PropertyStatus.OUT_OF_SERVICE) {
                    property.setStatus(PropertyStatus.VACANT);
                    propertyRepository.save(property);
                }
            }

        } catch (Exception e) {
            System.err.println("Error synchronizing property statuses: " + e.getMessage());
        }
    }
}