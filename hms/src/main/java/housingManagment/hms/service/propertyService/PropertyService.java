package housingManagment.hms.service.propertyService;

import housingManagment.hms.dto.PropertyListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PropertyService {
    Page<PropertyListDTO> getAllPropertiesFiltered(
            String propertyType,
            String block,
            String status,
            Boolean isVacant,
            Double minRent,
            Double maxRent,
            Integer minOccupants,
            Integer maxOccupants,
            String searchTerm,
            String sortBy,
            String sortDirection,
            Pageable pageable);

    List<String> getAllPropertyTypes();

    List<String> getAllBlocks();

    Map<String, Object> getPropertyStatistics();

    Map<String, Object> getOccupancyStatistics();

    void exportProperties(String propertyType, String block, String status, Boolean isVacant, String searchTerm);
}