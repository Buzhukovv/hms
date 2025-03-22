package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.DormitoryRoomRepository;
import housingManagment.hms.service.property.DormitoryRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DormitoryRoomServiceImpl implements DormitoryRoomService {

    private final DormitoryRoomRepository repository;

    @Override
    public DormitoryRoom createProperty(DormitoryRoom property) {

        return repository.save(property);
    }

    @Override
    public DormitoryRoom updateProperty(UUID id, DormitoryRoom updatedData) {
        DormitoryRoom existing = getPropertyById(id);
        existing.setPropertyBlock(updatedData.getPropertyBlock());
        existing.setStatus(updatedData.getStatus());
        existing.setIsPaid(updatedData.getIsPaid());
        existing.setRent(updatedData.getRent());
        existing.setDepositAmount(updatedData.getDepositAmount());
        return repository.save(existing);
    }

    @Override
    public void deleteProperty(UUID id) {
        DormitoryRoom property = getPropertyById(id);
        repository.delete(property);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "propertyQueries", key = "#id")
    public DormitoryRoom getPropertyById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DormitoryRoom not found with id: " + id));
    }

    @Override
    public List<DormitoryRoom> getAllProperties() {
        return repository.findAll();
    }

    @Override
    public List<DormitoryRoom> getPropertiesByStatus(PropertyStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<DormitoryRoom> searchProperties(String keyword) {
        return repository.searchProperties(keyword);
    }

    @Override
    public List<DormitoryRoom> getAvailableProperties() {
        return repository.findAvailableProperties();
    }

    @Override
    public DormitoryRoom updatePropertyStatus(UUID id, PropertyStatus status) {
        DormitoryRoom property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    public List<DormitoryRoom> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        return repository.findByRentBetween(minPrice, maxPrice);
    }
}
