package housingManagment.hms.service;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.enums.MaintenanceRequestStatus;

import java.util.List;
import java.util.UUID;

public interface MaintenanceRequestService {

    /**
     * Create a new maintenance request
     */
    MaintenanceRequestDTO createRequest(MaintenanceRequestDTO requestDTO);

    /**
     * Get a maintenance request by ID
     */
    MaintenanceRequestDTO getRequestById(UUID id);

    /**
     * Get a maintenance request by request number
     */
    MaintenanceRequestDTO getRequestByNumber(String requestNumber);

    /**
     * Get all maintenance requests for a specific user
     */
    List<MaintenanceRequestDTO> getRequestsByUser(UUID userId);

    /**
     * Get all maintenance requests for a specific lease
     */
    List<MaintenanceRequestDTO> getRequestsByLease(UUID leaseId);

    /**
     * Get all maintenance requests assigned to a specific maintenance staff
     */
    List<MaintenanceRequestDTO> getRequestsByAssignedStaff(UUID staffId);

    /**
     * Get all maintenance requests with a specific status
     */
    List<MaintenanceRequestDTO> getRequestsByStatus(MaintenanceRequestStatus status);

    /**
     * Update a maintenance request
     */
    MaintenanceRequestDTO updateRequest(UUID id, MaintenanceRequestDTO requestDTO);

    /**
     * Assign a maintenance request to staff
     */
    MaintenanceRequestDTO assignRequest(UUID requestId, UUID staffId);

    /**
     * Update the status of a maintenance request
     */
    MaintenanceRequestDTO updateRequestStatus(UUID requestId, MaintenanceRequestStatus status);

    /**
     * Mark a maintenance request as paid
     */
    MaintenanceRequestDTO markRequestAsPaid(UUID requestId);

    /**
     * Mark a maintenance request as completed
     */
    MaintenanceRequestDTO markRequestAsCompleted(UUID requestId);

    /**
     * Delete a maintenance request
     */
    void deleteRequest(UUID id);

    /**
     * Get all maintenance requests
     */
    List<MaintenanceRequestDTO> getAllRequests();
}