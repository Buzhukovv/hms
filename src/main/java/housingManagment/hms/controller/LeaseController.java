package housingManagment.hms.controller;

import housingManagment.hms.dto.LeaseCreateDTO;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import housingManagment.hms.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/leases")
@RequiredArgsConstructor
public class LeaseController {
    private final LeaseService leaseService;

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

    // Получение всех договоров аренды
    @GetMapping
    public String listLeases(Model model,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String search,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        LeaseStatus leaseStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                leaseStatus = LeaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Если передан неверный статус, оставляем фильтр пустым (или можно логировать ошибку)
            }
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Lease> leasePage = leaseService.getLeasesPaginated(pageable, leaseStatus, search);

        model.addAttribute("leases", leasePage.getContent());
        model.addAttribute("currentPage", leasePage.getNumber());
        model.addAttribute("totalPages", leasePage.getTotalPages());
        model.addAttribute("pageSize", leasePage.getSize());
        model.addAttribute("totalItems", leasePage.getTotalElements());
        model.addAttribute("availableSizes", List.of(10, 20, 30, 40));

        return "leases/list";
    }

    @GetMapping("/{id}")
    public String viewLease(@PathVariable UUID id, Model model) {
        Lease lease = leaseService.getLeaseById(id);
        model.addAttribute("lease", lease);
        return "leases/view";
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
