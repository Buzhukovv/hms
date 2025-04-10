package housingManagment.hms.controller;

import housingManagment.hms.dto.FamilyMemberDTO;
import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.service.LeaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
@Tag(name = "Leases Management", description = "APIs for managing leases")
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    public ResponseEntity<Lease> createLease(@RequestBody LeaseCreateDTO dto) {
        Lease createdLease = leaseService.createLease(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLease);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lease> updateLease(@PathVariable UUID id, @RequestBody Lease lease) {
        Lease updated = leaseService.updateLease(id, lease);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable UUID id) {
        leaseService.deleteLease(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listLeases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LeaseStatus leaseStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                leaseStatus = LeaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid lease status: " + status));
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Lease> leasePage = leaseService.getLeasesPaginated(pageable, leaseStatus, search);

        Map<String, Object> response = new HashMap<>();
        response.put("leases", leasePage.getContent());
        response.put("currentPage", leasePage.getNumber());
        response.put("totalPages", leasePage.getTotalPages());
        response.put("pageSize", leasePage.getSize());
        response.put("totalItems", leasePage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lease> viewLease(@PathVariable UUID id) {
        Lease lease = leaseService.getLeaseById(id);
        return ResponseEntity.ok(lease);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Lease>> getLeasesByProperty(@PathVariable UUID propertyId) {
        List<Lease> leases = leaseService.getLeasesByProperty(propertyId);
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/property/{propertyId}/active")
    public ResponseEntity<List<Lease>> getActiveLeasesByProperty(@PathVariable UUID propertyId) {
        List<Lease> leases = leaseService.getActiveLeasesByProperty(propertyId);
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Lease>> getLeasesByTenant(@PathVariable UUID tenantId) {
        List<Lease> leases = leaseService.getLeasesByTenant(tenantId);
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Lease>> getLeasesByStatus(@PathVariable String status) {
        try {
            LeaseStatus leaseStatus = LeaseStatus.valueOf(status.toUpperCase());
            List<Lease> leases = leaseService.getLeasesByStatus(leaseStatus);
            return ResponseEntity.ok(leases);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Lease>> getActiveLeases() {
        return ResponseEntity.ok(leaseService.getActiveLeases());
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Lease>> getExpiringLeases() {
        return ResponseEntity.ok(leaseService.getExpiringLeases());
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<Lease> renewLease(
            @PathVariable UUID id,
            @RequestParam("newEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        Lease renewed = leaseService.renewLease(id, newEndDate);
        return ResponseEntity.ok(renewed);
    }

    @PutMapping("/{id}/terminate")
    public ResponseEntity<Lease> terminateLease(
            @PathVariable UUID id,
            @RequestParam("terminationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate) {
        Lease terminated = leaseService.terminateLease(id, terminationDate);
        return ResponseEntity.ok(terminated);
    }

    @PutMapping("/batch-terminate")
    public ResponseEntity<List<Lease>> batchTerminateLeases(
            @RequestBody List<UUID> leaseIds,
            @RequestParam("terminationDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate) {
        if (leaseIds == null || leaseIds.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        List<Lease> terminatedLeases = leaseIds.stream()
                .map(id -> leaseService.terminateLease(id, terminationDate))
                .toList();
        return ResponseEntity.ok(terminatedLeases);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Lease>> searchLeases(@RequestParam String keyword) {
        return ResponseEntity.ok(leaseService.searchLeases(keyword));
    }

    @PostMapping("/{leaseId}/add-family-members")
    public ResponseEntity<Lease> addFamilyMembersToLease(
            @PathVariable UUID leaseId,
            @RequestBody List<FamilyMemberDTO> familyMemberDTOs) {
        Lease updatedLease = leaseService.addFamilyMembersToLease(leaseId, familyMemberDTOs);
        return ResponseEntity.ok(updatedLease);
    }
}