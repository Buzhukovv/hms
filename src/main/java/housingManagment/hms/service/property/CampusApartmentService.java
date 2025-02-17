package housingManagment.hms.service.property;


import housingManagment.hms.entities.property.CampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;

import java.util.List;
import java.util.UUID;

public interface CampusApartmentService {
    CampusApartment createProperty(CampusApartment property);
    CampusApartment updateProperty(UUID id, CampusApartment property);
    void deleteProperty(UUID id);
    CampusApartment getPropertyById(UUID id);
    List<CampusApartment> getAllProperties();
    List<CampusApartment> getPropertiesByStatus(PropertyStatus status);
    List<CampusApartment> searchProperties(String keyword);
    List<CampusApartment> getAvailableProperties();
    CampusApartment updatePropertyStatus(UUID id, PropertyStatus status);
    List<CampusApartment> getPropertiesByPriceRange(Double minPrice, Double maxPrice);
}
