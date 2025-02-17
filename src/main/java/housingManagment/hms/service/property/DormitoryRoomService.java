package housingManagment.hms.service.property;

import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.property.PropertyStatus;

import java.util.List;
import java.util.UUID;

public interface DormitoryRoomService {
    DormitoryRoom createProperty(DormitoryRoom property);
    DormitoryRoom updateProperty(UUID id, DormitoryRoom property);
    void deleteProperty(UUID id);
    DormitoryRoom getPropertyById(UUID id);
    List<DormitoryRoom> getAllProperties();
    List<DormitoryRoom> getPropertiesByStatus(PropertyStatus status);
    List<DormitoryRoom> searchProperties(String keyword);
    List<DormitoryRoom> getAvailableProperties();
    DormitoryRoom updatePropertyStatus(UUID id, PropertyStatus status);
    List<DormitoryRoom> getPropertiesByPriceRange(Double minPrice, Double maxPrice);
}
