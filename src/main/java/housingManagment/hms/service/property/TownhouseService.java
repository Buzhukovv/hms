package housingManagment.hms.service.property;


import housingManagment.hms.entities.property.Townhouse;
import housingManagment.hms.enums.property.PropertyStatus;

import java.util.List;
import java.util.UUID;

public interface TownhouseService {
    Townhouse createProperty(Townhouse property);
    Townhouse updateProperty(UUID id, Townhouse property);
    void deleteProperty(UUID id);
    Townhouse getPropertyById(UUID id);
    List<Townhouse> getAllProperties();
    List<Townhouse> getPropertiesByStatus(PropertyStatus status);
    List<Townhouse> searchProperties(String keyword);
    List<Townhouse> getAvailableProperties();
    Townhouse updatePropertyStatus(UUID id, PropertyStatus status);
    List<Townhouse> getPropertiesByPriceRange(Double minPrice, Double maxPrice);
}
