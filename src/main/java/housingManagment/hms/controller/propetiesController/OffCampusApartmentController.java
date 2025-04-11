package housingManagment.hms.controller.propetiesController;

import housingManagment.hms.entities.property.OffCampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.service.property.OffCampusApartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/off-campus-properties")
@RequiredArgsConstructor
@Tag(name = "Property Controller OFFCAMPUS")
public class OffCampusApartmentController {

    private final OffCampusApartmentService offCampusApartmentService;

    @PostMapping
    public ResponseEntity<OffCampusApartment> createProperty(@RequestBody OffCampusApartment property) {
        OffCampusApartment created = offCampusApartmentService.createProperty(property);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OffCampusApartment> updateProperty(@PathVariable UUID id,
                                                               @RequestBody OffCampusApartment property) {
        OffCampusApartment updated = offCampusApartmentService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        offCampusApartmentService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffCampusApartment> getPropertyById(@PathVariable UUID id) {
        OffCampusApartment property = offCampusApartmentService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<OffCampusApartment>> getAllProperties() {
        List<OffCampusApartment> properties = offCampusApartmentService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/status")
    public ResponseEntity<List<OffCampusApartment>> getPropertiesByStatus(@RequestParam("status") PropertyStatus status) {
        List<OffCampusApartment> properties = offCampusApartmentService.getPropertiesByStatus(status);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OffCampusApartment>> searchProperties(@RequestParam("q") String keyword) {
        List<OffCampusApartment> properties = offCampusApartmentService.searchProperties(keyword);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/available")
    public ResponseEntity<List<OffCampusApartment>> getAvailableProperties() {
        List<OffCampusApartment> properties = offCampusApartmentService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OffCampusApartment> updatePropertyStatus(@PathVariable UUID id,
                                                                   @RequestParam("status") PropertyStatus status) {
        OffCampusApartment property = offCampusApartmentService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<OffCampusApartment>> getPropertiesByPriceRange(@RequestParam("min") Double minPrice,
                                                                              @RequestParam("max") Double maxPrice) {
        List<OffCampusApartment> properties = offCampusApartmentService.getPropertiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }
}
