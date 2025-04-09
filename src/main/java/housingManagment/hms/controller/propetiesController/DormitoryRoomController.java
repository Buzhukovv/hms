package housingManagment.hms.controller.propetiesController;

import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.service.property.DormitoryRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dormitory-rooms")
@RequiredArgsConstructor
@Tag(name = "Property Controller")
public class DormitoryRoomController {

    private final DormitoryRoomService dormitoryRoomService;

    @PostMapping
    public ResponseEntity<DormitoryRoom> createProperty(@RequestBody DormitoryRoom property) {
        DormitoryRoom created = dormitoryRoomService.createProperty(property);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DormitoryRoom> updateProperty(@PathVariable UUID id,
                                                          @RequestBody DormitoryRoom property) {
        DormitoryRoom updated = dormitoryRoomService.updateProperty(id, property);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        dormitoryRoomService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DormitoryRoom> getPropertyById(@PathVariable UUID id) {
        DormitoryRoom property = dormitoryRoomService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<List<DormitoryRoom>> getAllProperties() {
        List<DormitoryRoom> properties = dormitoryRoomService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/status")
    public ResponseEntity<List<DormitoryRoom>> getPropertiesByStatus(@RequestParam("status") PropertyStatus status) {
        List<DormitoryRoom> properties = dormitoryRoomService.getPropertiesByStatus(status);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DormitoryRoom>> searchProperties(@RequestParam("q") String keyword) {
        List<DormitoryRoom> properties = dormitoryRoomService.searchProperties(keyword);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DormitoryRoom>> getAvailableProperties() {
        List<DormitoryRoom> properties = dormitoryRoomService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DormitoryRoom> updatePropertyStatus(@PathVariable UUID id,
                                                              @RequestParam("status") PropertyStatus status) {
        DormitoryRoom property = dormitoryRoomService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<DormitoryRoom>> getPropertiesByPriceRange(@RequestParam("min") Double minPrice,
                                                                         @RequestParam("max") Double maxPrice) {
        List<DormitoryRoom> properties = dormitoryRoomService.getPropertiesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(properties);
    }
}
