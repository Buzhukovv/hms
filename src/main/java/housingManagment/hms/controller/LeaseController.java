package housingManagment.hms.controller;

import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;

    // Создание нового договора аренды
    @PostMapping
    public Lease createLease(@RequestBody LeaseCreateDTO dto) {
        return leaseService.createLease(dto);
    }
    // Обновление договора аренды по id
    @PutMapping("/{id}")
    public Lease updateLease(@PathVariable UUID id, @RequestBody Lease lease) {
        return leaseService.updateLease(id, lease);
    }

    // Удаление договора аренды по id
    @DeleteMapping("/{id}")
    public void deleteLease(@PathVariable UUID id) {
        leaseService.deleteLease(id);
    }

    // Получение договора аренды по id
    @GetMapping("/{id}")
    public Lease getLeaseById(@PathVariable UUID id) {
        return leaseService.getLeaseById(id);
    }

    // Получение всех договоров аренды
    @GetMapping
    public List<Lease> getAllLeases() {
        return leaseService.getAllLeases();
    }


    @GetMapping("/property/{propertyId}")
    public List<BaseProperty> getUniquePropertiesByLease(@PathVariable UUID propertyId) {
        return leaseService.getLeasesByProperty(propertyId)
                .stream()
                .map(Lease::getProperty)
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/tenant/{tenantId}")
    public List<BaseUser> getUniqueTenantsByLease(@PathVariable UUID tenantId) {
        return leaseService.getLeasesByTenant(tenantId)
                .stream()
                .map(Lease::getTenant)
                .distinct()
                .collect(Collectors.toList());
    }

    // Получение договоров с заданным статусом
    @GetMapping("/status/{status}")
    public List<Lease> getLeasesByStatus(@PathVariable LeaseStatus status) {
        return leaseService.getLeasesByStatus(status);
    }

    // Получение активных договоров
    @GetMapping("/active")
    public List<Lease> getActiveLeases() {
        return leaseService.getActiveLeases();
    }

    // Получение договоров, срок действия которых истекает
    @GetMapping("/expiring")
    public List<Lease> getExpiringLeases() {
        return leaseService.getExpiringLeases();
    }

    // Продление договора аренды: обновление даты окончания
    @PutMapping("/{id}/renew")
    public Lease renewLease(@PathVariable UUID id,
                            @RequestParam("newEndDate") 
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newEndDate) {
        return leaseService.renewLease(id, newEndDate);
    }

    @PutMapping("/{id}/terminate")
    public Lease terminateLease(@PathVariable UUID id,
                                @RequestParam("terminationDate")
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate terminationDate) {
        return leaseService.terminateLease(id, terminationDate);
    }

    // Поиск договоров по ключевому слову
    @GetMapping("/search")
    public List<Lease> searchLeases(@RequestParam String keyword) {
        return leaseService.searchLeases(keyword);
    }
}
