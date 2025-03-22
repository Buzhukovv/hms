package housingManagment.hms.controller;

import housingManagment.hms.dto.MaintenanceRequestDTO;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @PostMapping
    public ResponseEntity<MaintenanceRequestDTO> createRequest(@Valid @RequestBody MaintenanceRequestDTO requestDTO) {
        return new ResponseEntity<>(maintenanceRequestService.createRequest(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestDTO> getRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestById(id));
    }

    @GetMapping("/number/{requestNumber}")
    public ResponseEntity<MaintenanceRequestDTO> getRequestByNumber(@PathVariable String requestNumber) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestByNumber(requestNumber));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByUser(userId));
    }

    @GetMapping("/lease/{leaseId}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsByLease(@PathVariable UUID leaseId) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByLease(leaseId));
    }

    @GetMapping("/assigned-to/{staffId}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsByAssignedStaff(@PathVariable UUID staffId) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByAssignedStaff(staffId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MaintenanceRequestDTO>> getRequestsByStatus(
            @PathVariable MaintenanceRequestStatus status) {
        return ResponseEntity.ok(maintenanceRequestService.getRequestsByStatus(status));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(maintenanceRequestService.getAllRequests());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequestDTO> updateRequest(
            @PathVariable UUID id, @Valid @RequestBody MaintenanceRequestDTO requestDTO) {
        return ResponseEntity.ok(maintenanceRequestService.updateRequest(id, requestDTO));
    }

    @PutMapping("/{requestId}/assign/{staffId}")
    public ResponseEntity<MaintenanceRequestDTO> assignRequest(
            @PathVariable UUID requestId, @PathVariable UUID staffId) {
        return ResponseEntity.ok(maintenanceRequestService.assignRequest(requestId, staffId));
    }

    @PutMapping("/{requestId}/status/{status}")
    public ResponseEntity<MaintenanceRequestDTO> updateRequestStatus(
            @PathVariable UUID requestId, @PathVariable MaintenanceRequestStatus status) {
        return ResponseEntity.ok(maintenanceRequestService.updateRequestStatus(requestId, status));
    }

    @PutMapping("/{requestId}/mark-paid")
    public ResponseEntity<MaintenanceRequestDTO> markRequestAsPaid(@PathVariable UUID requestId) {
        return ResponseEntity.ok(maintenanceRequestService.markRequestAsPaid(requestId));
    }

    @PutMapping("/{requestId}/mark-completed")
    public ResponseEntity<MaintenanceRequestDTO> markRequestAsCompleted(@PathVariable UUID requestId) {
        return ResponseEntity.ok(maintenanceRequestService.markRequestAsCompleted(requestId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        maintenanceRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}