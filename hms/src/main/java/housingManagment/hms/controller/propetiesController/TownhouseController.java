package housingManagment.hms.controller.propetiesController;


import housingManagment.hms.entities.property.Townhouse;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.service.property.TownhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/townhouses")
@RequiredArgsConstructor
public class TownhouseController {

    private final TownhouseService townhouseService;

    @PostMapping
    public ResponseEntity<Townhouse> createProperty(@RequestBody Townhouse property) {
        Townhouse created = townhouseService.createProperty(property);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Townhouse> updateProperty(@PathVariable UUID id,
                                                    @RequestBody Townhouse property) {
        Townhouse updated = townhouseService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        townhouseService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Townhouse> getPropertyById(@PathVariable UUID id) {
        Townhouse property = townhouseService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<Townhouse>> getAllProperties() {
        List<Townhouse> properties = townhouseService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Townhouse>> getPropertiesByStatus(@RequestParam("status") PropertyStatus status) {
        List<Townhouse> properties = townhouseService.getPropertiesByStatus(status);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Townhouse>> searchProperties(@RequestParam("q") String keyword) {
        List<Townhouse> properties = townhouseService.searchProperties(keyword);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Townhouse>> getAvailableProperties() {
        List<Townhouse> properties = townhouseService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Townhouse> updatePropertyStatus(@PathVariable UUID id,
                                                        @RequestParam("status") PropertyStatus status) {
        Townhouse property = townhouseService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Townhouse>> getPropertiesByPriceRange(@RequestParam("min") Double minPrice,
                                                                   @RequestParam("max") Double maxPrice) {
        List<Townhouse> properties = townhouseService.getPropertiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }
}
