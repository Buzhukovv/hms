package housingManagment.hms.controller.propetiesController;

import housingManagment.hms.entities.property.CampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.service.property.CampusApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/campus-apartments")
@RequiredArgsConstructor
public class CampusApartmentController {

    private final CampusApartmentService campusApartmentService;

    @PostMapping
    public ResponseEntity<CampusApartment> createProperty(@RequestBody CampusApartment property) {
        CampusApartment created = campusApartmentService.createProperty(property);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampusApartment> updateProperty(@PathVariable UUID id,
                                                            @RequestBody CampusApartment property) {
        CampusApartment updated = campusApartmentService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        campusApartmentService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampusApartment> getPropertyById(@PathVariable UUID id) {
        CampusApartment property = campusApartmentService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<CampusApartment>> getAllProperties() {
        List<CampusApartment> properties = campusApartmentService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/status")
    public ResponseEntity<List<CampusApartment>> getPropertiesByStatus(@RequestParam("status") PropertyStatus status) {
        List<CampusApartment> properties = campusApartmentService.getPropertiesByStatus(status);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CampusApartment>> searchProperties(@RequestParam("q") String keyword) {
        List<CampusApartment> properties = campusApartmentService.searchProperties(keyword);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/available")
    public ResponseEntity<List<CampusApartment>> getAvailableProperties() {
        List<CampusApartment> properties = campusApartmentService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CampusApartment> updatePropertyStatus(@PathVariable UUID id,
                                                                @RequestParam("status") PropertyStatus status) {
        CampusApartment property = campusApartmentService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<CampusApartment>> getPropertiesByPriceRange(@RequestParam("min") Double minPrice,
                                                                           @RequestParam("max") Double maxPrice) {
        List<CampusApartment> properties = campusApartmentService.getPropertiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }
}
