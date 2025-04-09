package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.OffCampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.OffCampusApartmentRepository;
import housingManagment.hms.service.property.OffCampusApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// TODO : Check all of the endpoints and add if business logic needs new endpoints

@Service
@RequiredArgsConstructor
@Transactional
public class OffCampusApartmentServiceImpl implements OffCampusApartmentService {

    private final OffCampusApartmentRepository repository;

    @Override
    public OffCampusApartment createProperty(OffCampusApartment property) {

        return repository.save(property);
    }

    @Override
    public OffCampusApartment updateProperty(UUID id, OffCampusApartment updatedData) {
        OffCampusApartment existing = getPropertyById(id);
        existing.setPropertyBlock(updatedData.getPropertyBlock());
        existing.setStatus(updatedData.getStatus());
        existing.setArea(updatedData.getArea());
        existing.setIsPaid(updatedData.getIsPaid());
        existing.setRent(updatedData.getRent());
        existing.setDepositAmount(updatedData.getDepositAmount());
        existing.setAddress(updatedData.getAddress());
        existing.setMaxOccupant(updatedData.getMaxOccupant());

        return repository.save(existing);
    }

    @Override
    public void deleteProperty(UUID id) {
        OffCampusApartment property = getPropertyById(id);
        repository.delete(property);
    }

    @Override
    public OffCampusApartment getPropertyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OffCampusApartment not found with id: " + id));
    }

    @Override
    public List<OffCampusApartment> getAllProperties() {
        return repository.findAll();
    }

    @Override
    public List<OffCampusApartment> getPropertiesByStatus(PropertyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<OffCampusApartment> searchProperties(String keyword) {
        return repository.searchProperties(keyword);
    }

    @Override
    public List<OffCampusApartment> getAvailableProperties() {
        return repository.findAvailableProperties();
    }

    @Override
    public OffCampusApartment updatePropertyStatus(UUID id, PropertyStatus status) {
        OffCampusApartment property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    public List<OffCampusApartment> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        return repository.findByRentBetween(minPrice, maxPrice);
    }
}
