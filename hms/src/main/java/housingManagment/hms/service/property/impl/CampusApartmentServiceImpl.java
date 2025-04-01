package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.CampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.CampusApartmentRepository;
import housingManagment.hms.service.property.CampusApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CampusApartmentServiceImpl implements CampusApartmentService {

    private final CampusApartmentRepository repository;

    @Override
    public CampusApartment createProperty(CampusApartment property) {
        // Для каждого элемента инвентаря устанавливаем связь с текущим объектом
        return repository.save(property);
    }

    @Override
    public CampusApartment updateProperty(UUID id, CampusApartment updatedData) {
        CampusApartment existing = getPropertyById(id);
        existing.setPropertyBlock(updatedData.getPropertyBlock());
        existing.setStatus(updatedData.getStatus());
        existing.setArea(updatedData.getArea());
        existing.setIsPaid(updatedData.getIsPaid());
        existing.setRent(updatedData.getRent());
        existing.setDepositAmount(updatedData.getDepositAmount());
        // Обновляем коллекцию инвентаря: очищаем старую и добавляем новые, устанавливая связь
        return repository.save(existing);
    }

    @Override
    public void deleteProperty(UUID id) {
        CampusApartment property = getPropertyById(id);
        repository.delete(property);
    }

    @Override
    public CampusApartment getPropertyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampusApartment not found with id: " + id));
    }

    @Override
    public List<CampusApartment> getAllProperties() {
        return repository.findAll();
    }

    @Override
    public List<CampusApartment> getPropertiesByStatus(PropertyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<CampusApartment> searchProperties(String keyword) {
        return repository.searchProperties(keyword);
    }

    @Override
    public List<CampusApartment> getAvailableProperties() {
        return repository.findAvailableProperties();
    }

    @Override
    public CampusApartment updatePropertyStatus(UUID id, PropertyStatus status) {
        CampusApartment property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    public List<CampusApartment> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        return repository.findByRentBetween(minPrice, maxPrice);
    }

}
