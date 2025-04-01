package housingManagment.hms.service.impl;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.MaintenanceRequest;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.exception.EntityNotFoundException;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.MaintenanceRequestRepository;
import housingManagment.hms.repository.userRepository.UserRepository;
import housingManagment.hms.service.MaintenanceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UserRepository userRepository;
    private final LeaseRepository leaseRepository;

    @Override
    @Transactional
    public MaintenanceRequestDTO createRequest(MaintenanceRequestDTO requestDTO) {
        // Verify the requester exists
        var requesterOptional = userRepository.findById(requestDTO.getRequesterId());
        if (requesterOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found with ID: " + requestDTO.getRequesterId());
        }
        BaseUser requester = (BaseUser) requesterOptional.get();

        // Verify the lease exists and is associated with the requester
        Lease lease = leaseRepository.findById(requestDTO.getLeaseId())
                .orElseThrow(() -> new EntityNotFoundException("Lease not found with ID: " + requestDTO.getLeaseId()));

        // Verify the requester is the tenant of the lease
        if (!lease.getTenant().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("The requester is not the tenant of the specified lease");
        }

        MaintenanceRequest maintenanceRequest = MaintenanceRequest.builder()
                .requester(requester)
                .lease(lease)
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .status(MaintenanceRequestStatus.PENDING)
                .isPaid(false)
                .serviceCharge(requestDTO.getServiceCharge())
                .notes(requestDTO.getNotes())
                .build();

        MaintenanceRequest savedRequest = maintenanceRequestRepository.save(maintenanceRequest);
        return convertToDTO(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceRequestDTO getRequestById(UUID id) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + id));
        return convertToDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceRequestDTO getRequestByNumber(String requestNumber) {
        MaintenanceRequest request = maintenanceRequestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Maintenance request not found with number: " + requestNumber));
        return convertToDTO(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestDTO> getRequestsByUser(UUID userId) {
        return maintenanceRequestRepository.findByRequesterId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestDTO> getRequestsByLease(UUID leaseId) {
        return maintenanceRequestRepository.findByLeaseId(leaseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestDTO> getRequestsByAssignedStaff(UUID staffId) {
        Optional<?> optionalStaff = userRepository.findById(staffId);
        if (optionalStaff.isEmpty()) {
            throw new EntityNotFoundException("Maintenance staff not found with ID: " + staffId);
        }
        Maintenance staff = (Maintenance) optionalStaff.get();

        if (!(staff instanceof Maintenance)) {
            throw new IllegalArgumentException("The specified user is not a maintenance staff");
        }

        return maintenanceRequestRepository.findByAssignedTo(staff)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestDTO> getRequestsByStatus(MaintenanceRequestStatus status) {
        return maintenanceRequestRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaintenanceRequestDTO updateRequest(UUID id, MaintenanceRequestDTO requestDTO) {
        MaintenanceRequest existingRequest = maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + id));

        // Only allow updating certain fields
        if (requestDTO.getTitle() != null) {
            existingRequest.setTitle(requestDTO.getTitle());
        }

        if (requestDTO.getDescription() != null) {
            existingRequest.setDescription(requestDTO.getDescription());
        }

        if (requestDTO.getNotes() != null) {
            existingRequest.setNotes(requestDTO.getNotes());
        }

        if (requestDTO.getServiceCharge() != null) {
            existingRequest.setServiceCharge(requestDTO.getServiceCharge());
        }

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(existingRequest);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public MaintenanceRequestDTO assignRequest(UUID requestId, UUID staffId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + requestId));

        Optional<?> optionalStaff = userRepository.findById(staffId);
        if (optionalStaff.isEmpty()) {
            throw new EntityNotFoundException("Maintenance staff not found with ID: " + staffId);
        }
        Maintenance staff = (Maintenance) optionalStaff.get();

        if (!(staff instanceof Maintenance)) {
            throw new IllegalArgumentException("The specified user is not a maintenance staff");
        }

        request.setAssignedTo(staff);

        // If the request is pending, update it to approved (waiting for payment)
        if (request.getStatus() == MaintenanceRequestStatus.PENDING) {
            request.setStatus(MaintenanceRequestStatus.APPROVED);
        }

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public MaintenanceRequestDTO updateRequestStatus(UUID requestId, MaintenanceRequestStatus status) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + requestId));

        // Apply business rules for status transitions
        if (status == MaintenanceRequestStatus.IN_PROGRESS && !request.getIsPaid()) {
            throw new IllegalStateException("Cannot start work on a request that hasn't been paid for");
        }

        if (status == MaintenanceRequestStatus.COMPLETED) {
            request.setCompletedAt(LocalDateTime.now());
        }

        request.setStatus(status);
        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public MaintenanceRequestDTO markRequestAsPaid(UUID requestId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + requestId));

        request.setIsPaid(true);
        request.setStatus(MaintenanceRequestStatus.PAID);

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public MaintenanceRequestDTO markRequestAsCompleted(UUID requestId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance request not found with ID: " + requestId));

        if (!request.getIsPaid()) {
            throw new IllegalStateException("Cannot mark an unpaid request as completed");
        }

        request.setStatus(MaintenanceRequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());

        MaintenanceRequest updatedRequest = maintenanceRequestRepository.save(request);
        return convertToDTO(updatedRequest);
    }

    @Override
    @Transactional
    public void deleteRequest(UUID id) {
        if (!maintenanceRequestRepository.existsById(id)) {
            throw new EntityNotFoundException("Maintenance request not found with ID: " + id);
        }

        maintenanceRequestRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceRequestDTO> getAllRequests() {
        return maintenanceRequestRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return maintenanceRequestRepository.count();
    }

    /**
     * Helper method to convert MaintenanceRequest entity to DTO
     */
    private MaintenanceRequestDTO convertToDTO(MaintenanceRequest request) {
        // Extract property information from lease if available
        UUID propertyId = null;
        String propertyNumber = null;

        if (request.getLease() != null && request.getLease().getProperty() != null) {
            propertyId = request.getLease().getProperty().getId();
            propertyNumber = request.getLease().getProperty().getPropertyNumber();
        }

        return MaintenanceRequestDTO.builder()
                .id(request.getId())
                .requestNumber(request.getRequestNumber())
                .requesterId(request.getRequester() != null ? request.getRequester().getId() : null)
                .leaseId(request.getLease() != null ? request.getLease().getId() : null)
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedToId(request.getAssignedTo() != null ? request.getAssignedTo().getId() : null)
                .status(request.getStatus())
                .isPaid(request.getIsPaid())
                .serviceCharge(request.getServiceCharge())
                .notes(request.getNotes())
                .propertyId(propertyId)
                .propertyNumber(propertyNumber)
                .completedAt(request.getCompletedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}