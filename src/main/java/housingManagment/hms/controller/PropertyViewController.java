package housingManagment.hms.controller;

import housingManagment.hms.dto.PropertyListDTO;
import housingManagment.hms.service.propertyService.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyViewController {

    private final PropertyService propertyService;

    @GetMapping
    public Page<PropertyListDTO> getAllProperties(
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String block,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isVacant,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(required = false) Integer minOccupants,
            @RequestParam(required = false) Integer maxOccupants,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {
        return propertyService.getAllPropertiesFiltered(
                propertyType, block, status, isVacant, minRent, maxRent,
                minOccupants, maxOccupants, searchTerm, sortBy, sortDirection, pageable);
    }

    @GetMapping("/types")
    public List<String> getPropertyTypes() {
        return propertyService.getAllPropertyTypes();
    }

    @GetMapping("/blocks")
    public List<String> getBlocks() {
        return propertyService.getAllBlocks();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getPropertyStatistics() {
        return propertyService.getPropertyStatistics();
    }

    @GetMapping("/occupancy")
    public Map<String, Object> getOccupancyStatistics() {
        return propertyService.getOccupancyStatistics();
    }

    @GetMapping("/export")
    public void exportProperties(
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String block,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isVacant,
            @RequestParam(required = false) String searchTerm) {
        propertyService.exportProperties(propertyType, block, status, isVacant, searchTerm);
    }
}