package housingManagment.hms.controller.propetiesController;


import housingManagment.hms.entities.property.Cottage;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.service.property.CottageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cottages")
@RequiredArgsConstructor
@Tag(name = "Property Controller")
public class CottageController {

    private final CottageService cottageService;

    @PostMapping
    public ResponseEntity<Cottage> createProperty(@RequestBody Cottage property) {
        Cottage created = cottageService.createProperty(property);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cottage> updateProperty(@PathVariable UUID id,
                                                    @RequestBody Cottage property) {
        Cottage updated = cottageService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        cottageService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cottage> getPropertyById(@PathVariable UUID id) {
        Cottage property = cottageService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<Cottage>> getAllProperties() {
        List<Cottage> properties = cottageService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Cottage>> getPropertiesByStatus(@RequestParam("status") PropertyStatus status) {
        List<Cottage> properties = cottageService.getPropertiesByStatus(status);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cottage>> searchProperties(@RequestParam("q") String keyword) {
        List<Cottage> properties = cottageService.searchProperties(keyword);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Cottage>> getAvailableProperties() {
        List<Cottage> properties = cottageService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Cottage> updatePropertyStatus(@PathVariable UUID id,
                                                        @RequestParam("status") PropertyStatus status) {
        Cottage property = cottageService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Cottage>> getPropertiesByPriceRange(@RequestParam("min") Double minPrice,
                                                                   @RequestParam("max") Double maxPrice) {
        List<Cottage> properties = cottageService.getPropertiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }
}
