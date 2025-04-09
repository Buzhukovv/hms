package housingManagment.hms.service.propertyService.impl;

import housingManagment.hms.dto.PropertyListDTO;
import housingManagment.hms.entities.property.*;
import housingManagment.hms.enums.property.*;
import housingManagment.hms.service.propertyService.PropertyService;
import housingManagment.hms.repository.propertyRepository.PropertyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO : Get rid of this Huinya

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyListDTO> getAllPropertiesFiltered(
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
            Pageable pageable) {

        List<BaseProperty> properties = propertyRepository.findAll();
        Stream<BaseProperty> propertyStream = properties.stream();

        // Apply filters
        if (propertyType != null && !propertyType.isEmpty()) {
            propertyStream = propertyStream.filter(prop -> getPropertyType(prop).equals(propertyType));
        }

        if (block != null && !block.isEmpty()) {
            propertyStream = propertyStream.filter(prop -> prop.getPropertyBlock().equals(block));
        }

        if (status != null && !status.isEmpty()) {
            PropertyStatus propertyStatus = PropertyStatus.valueOf(status);
            propertyStream = propertyStream.filter(prop -> prop.getStatus() == propertyStatus);
        }

        if (isVacant != null) {
            propertyStream = propertyStream.filter(prop -> isVacant ? prop.getStatus() == PropertyStatus.VACANT
                    : prop.getStatus() != PropertyStatus.VACANT);
        }

        if (minRent != null) {
            propertyStream = propertyStream.filter(prop -> prop.getRent() >= minRent);
        }

        if (maxRent != null) {
            propertyStream = propertyStream.filter(prop -> prop.getRent() <= maxRent);
        }

        if (minOccupants != null) {
            propertyStream = propertyStream.filter(prop -> prop.getMaxOccupant() >= minOccupants);
        }

        if (maxOccupants != null) {
            propertyStream = propertyStream.filter(prop -> prop.getMaxOccupant() <= maxOccupants);
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String searchLower = searchTerm.toLowerCase();
            propertyStream = propertyStream
                    .filter(prop -> prop.getPropertyNumber().toLowerCase().contains(searchLower) ||
                            prop.getPropertyBlock().toLowerCase().contains(searchLower));
        }

        // Convert to DTOs
        List<PropertyListDTO> dtos = propertyStream
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // Apply sorting
        if (sortBy != null && sortDirection != null) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            dtos = sortProperties(dtos, sortBy, direction);
        }

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        return new PageImpl<>(
                dtos.subList(start, end),
                pageable,
                dtos.size());
    }

    @Override
    public List<String> getAllPropertyTypes() {
        return List.of("DormitoryRoom", "Cottage", "OffCampusApartment");
    }

    @Override
    public List<String> getAllBlocks() {
        return propertyRepository.findAll().stream()
                .map(BaseProperty::getPropertyBlock)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getPropertyStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<BaseProperty> properties = propertyRepository.findAll();

        // Count by property type
        Map<String, Long> propertyTypeCounts = properties.stream()
                .collect(Collectors.groupingBy(
                        this::getPropertyType,
                        Collectors.counting()));
        stats.put("propertyTypeCounts", propertyTypeCounts);

        // Count by status
        Map<String, Long> statusCounts = properties.stream()
                .collect(Collectors.groupingBy(
                        prop -> prop.getStatus().name(),
                        Collectors.counting()));
        stats.put("statusCounts", statusCounts);

        // Average rent by property type
        Map<String, Double> averageRents = properties.stream()
                .collect(Collectors.groupingBy(
                        this::getPropertyType,
                        Collectors.averagingDouble(BaseProperty::getRent)));
        stats.put("averageRents", averageRents);

        // Average area by property type
        Map<String, Double> averageAreas = properties.stream()
                .filter(prop -> prop.getArea() != null)
                .collect(Collectors.groupingBy(
                        this::getPropertyType,
                        Collectors.averagingDouble(BaseProperty::getArea)));
        stats.put("averageAreas", averageAreas);

        // Total area by property type
        Map<String, Double> totalAreas = properties.stream()
                .filter(prop -> prop.getArea() != null)
                .collect(Collectors.groupingBy(
                        this::getPropertyType,
                        Collectors.summingDouble(BaseProperty::getArea)));
        stats.put("totalAreas", totalAreas);

        return stats;
    }

    @Override
    public Map<String, Object> getOccupancyStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Calculate occupancy rates by property type
        Map<String, Double> occupancyRates = propertyRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        this::getPropertyType,
                        Collectors.averagingDouble(prop -> prop.getStatus() == PropertyStatus.VACANT ? 0.0 : 1.0)));
        stats.put("occupancyRates", occupancyRates);

        return stats;
    }

    @Override
    public void exportProperties(String propertyType, String block, String status, Boolean isVacant,
            String searchTerm) {
        // Implementation for exporting properties to CSV/Excel
        // This would typically use a library like Apache POI for Excel export
    }

    // Helper methods
    private PropertyListDTO convertToDTO(BaseProperty property) {
        PropertyListDTO dto = new PropertyListDTO();
        dto.setId(property.getId());
        dto.setPropertyNumber(property.getPropertyNumber());
        dto.setPropertyBlock(property.getPropertyBlock());
        dto.setPropertyType(getPropertyType(property));
        dto.setSpecificType(getSpecificType(property));
        dto.setMaxOccupant(property.getMaxOccupant());
        dto.setRent(property.getRent());
        dto.setDepositAmount(property.getDepositAmount());
        dto.setArea(property.getArea());
        dto.setStatus(property.getStatus());
        dto.setIsPaid(property.getIsPaid());

        if (property instanceof OffCampusApartment) {
            dto.setAddress(((OffCampusApartment) property).getAddress());
        }

        // You might want to implement a method to get current occupants count
        dto.setCurrentOccupants("0/" + property.getMaxOccupant());

        return dto;
    }

    private String getPropertyType(BaseProperty property) {
        if (property instanceof DormitoryRoom)
            return "DormitoryRoom";
        if (property instanceof Cottage)
            return "Cottage";
        if (property instanceof OffCampusApartment)
            return "OffCampusApartment";
        return "Unknown";
    }

    private String getSpecificType(BaseProperty property) {
        if (property instanceof DormitoryRoom) {
            return ((DormitoryRoom) property).getRoomType().name();
        }
        if (property instanceof OffCampusApartment) {
            return ((OffCampusApartment) property).getOffCampusType().name();
        }
        return "N/A";
    }

    private List<PropertyListDTO> sortProperties(List<PropertyListDTO> properties, String sortBy,
            Sort.Direction direction) {
        Comparator<PropertyListDTO> comparator = switch (sortBy) {
            case "propertyNumber" -> Comparator.comparing(PropertyListDTO::getPropertyNumber);
            case "propertyBlock" -> Comparator.comparing(PropertyListDTO::getPropertyBlock);
            case "propertyType" -> Comparator.comparing(PropertyListDTO::getPropertyType);
            case "rent" -> Comparator.comparing(PropertyListDTO::getRent);
            case "maxOccupant" -> Comparator.comparing(PropertyListDTO::getMaxOccupant);
            case "status" -> Comparator.comparing(prop -> prop.getStatus().name());
            case "area" -> Comparator.comparing(PropertyListDTO::getArea);
            default -> Comparator.comparing(PropertyListDTO::getPropertyNumber);
        };

        if (direction == Sort.Direction.DESC) {
            comparator = comparator.reversed();
        }

        return properties.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}