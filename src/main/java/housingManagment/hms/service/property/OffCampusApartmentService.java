package housingManagment.hms.service.property;

import housingManagment.hms.entities.property.OffCampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;

import java.util.List;
import java.util.UUID;

public interface OffCampusApartmentService {
    OffCampusApartment createProperty(OffCampusApartment property);
    OffCampusApartment updateProperty(UUID id, OffCampusApartment property);
    void deleteProperty(UUID id);
    OffCampusApartment getPropertyById(UUID id);
    List<OffCampusApartment> getAllProperties();
    List<OffCampusApartment> getPropertiesByStatus(PropertyStatus status);
    List<OffCampusApartment> searchProperties(String keyword);
    List<OffCampusApartment> getAvailableProperties();
    OffCampusApartment updatePropertyStatus(UUID id, PropertyStatus status);
    List<OffCampusApartment> getPropertiesByPriceRange(Double minPrice, Double maxPrice);
}
