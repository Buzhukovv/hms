package housingManagment.hms.service.property;


import housingManagment.hms.entities.property.Cottage;
import housingManagment.hms.enums.property.PropertyStatus;

import java.util.List;
import java.util.UUID;

public interface CottageService {
    Cottage createProperty(Cottage property);
    Cottage updateProperty(UUID id, Cottage property);
    void deleteProperty(UUID id);
    Cottage getPropertyById(UUID id);
    List<Cottage> getAllProperties();
    List<Cottage> getPropertiesByStatus(PropertyStatus status);
    List<Cottage> searchProperties(String keyword);
    List<Cottage> getAvailableProperties();
    Cottage updatePropertyStatus(UUID id, PropertyStatus status);
    List<Cottage> getPropertiesByPriceRange(Double minPrice, Double maxPrice);
}
