package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.Cottage;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.CottageRepository;
import housingManagment.hms.service.property.CottageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CottageServiceImpl implements CottageService {

    private final CottageRepository repository;

    @Override
    public Cottage createProperty(Cottage property) {
        return repository.save(property);
    }

    @Override
    public Cottage updateProperty(UUID id, Cottage updatedData) {
        Cottage existing = getPropertyById(id);
        existing.setPropertyBlock(updatedData.getPropertyBlock());
        existing.setStatus(updatedData.getStatus());
        existing.setArea(updatedData.getArea());
        existing.setIsPaid(updatedData.getIsPaid());
        existing.setRent(updatedData.getRent());
        existing.setDepositAmount(updatedData.getDepositAmount());

        return repository.save(existing);
    }

    @Override
    public void deleteProperty(UUID id) {
        Cottage property = getPropertyById(id);
        repository.delete(property);
    }

    @Override
    public Cottage getPropertyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cottage not found with id: " + id));
    }

    @Override
    public List<Cottage> getAllProperties() {
        return repository.findAll();
    }

    @Override
    public List<Cottage> getPropertiesByStatus(PropertyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<Cottage> searchProperties(String keyword) {
        return repository.searchProperties(keyword);
    }

    @Override
    public List<Cottage> getAvailableProperties() {
        return repository.findAvailableProperties();
    }

    @Override
    public Cottage updatePropertyStatus(UUID id, PropertyStatus status) {
        Cottage property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    public List<Cottage> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        return repository.findByRentBetween(minPrice, maxPrice);
    }
}
