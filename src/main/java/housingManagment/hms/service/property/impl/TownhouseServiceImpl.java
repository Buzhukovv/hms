package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.Townhouse;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.TownhouseRepository;
import housingManagment.hms.service.property.TownhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class TownhouseServiceImpl implements TownhouseService {

    private final TownhouseRepository repository;

    @Override
    public Townhouse createProperty(Townhouse property) {
        return repository.save(property);
    }

    @Override
    public Townhouse updateProperty(UUID id, Townhouse updatedData) {
        Townhouse existing = getPropertyById(id);
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
        Townhouse property = getPropertyById(id);
        repository.delete(property);
    }

    @Override
    public Townhouse getPropertyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Townhouse not found with id: " + id));
    }

    @Override
    public List<Townhouse> getAllProperties() {
        return repository.findAll();
    }

    @Override
    public List<Townhouse> getPropertiesByStatus(PropertyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<Townhouse> searchProperties(String keyword) {
        return repository.searchProperties(keyword);
    }

    @Override
    public List<Townhouse> getAvailableProperties() {
        return repository.findAvailableProperties();
    }

    @Override
    public Townhouse updatePropertyStatus(UUID id, PropertyStatus status) {
        Townhouse property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    public List<Townhouse> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        return repository.findByRentBetween(minPrice, maxPrice);
    }
}
