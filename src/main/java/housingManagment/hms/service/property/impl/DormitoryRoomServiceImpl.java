package housingManagment.hms.service.property.impl;

import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.property.PropertyStatus;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.propertyRepository.DormitoryRoomRepository;
import housingManagment.hms.service.property.DormitoryRoomService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.UUID;

// TODO : Check all of the endpoints and add if business logic needs new endpoints

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
        DormitoryRoom room = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DormitoryRoom not found with id: " + id));

        // Initialize any lazy-loaded collections
        Hibernate.initialize(room);

        return room;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DormitoryRoom> getAllProperties() {
        List<DormitoryRoom> rooms = repository.findAll();

        // Initialize any lazy-loaded collections for each room
        for (DormitoryRoom room : rooms) {
            Hibernate.initialize(room);
        }

        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DormitoryRoom> getPropertiesByStatus(PropertyStatus status) {
        List<DormitoryRoom> rooms = repository.findByStatus(status);

        // Initialize any lazy-loaded collections for each room
        for (DormitoryRoom room : rooms) {
            Hibernate.initialize(room);
        }

        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DormitoryRoom> searchProperties(String keyword) {
        List<DormitoryRoom> rooms = repository.searchProperties(keyword);

        // Initialize any lazy-loaded collections for each room
        for (DormitoryRoom room : rooms) {
            Hibernate.initialize(room);
        }

        return rooms;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DormitoryRoom> getAvailableProperties() {
        List<DormitoryRoom> rooms = repository.findAvailableProperties();

        // Initialize any lazy-loaded collections for each room
        for (DormitoryRoom room : rooms) {
            Hibernate.initialize(room);
        }

        return rooms;
    }

    @Override
    public DormitoryRoom updatePropertyStatus(UUID id, PropertyStatus status) {
        DormitoryRoom property = getPropertyById(id);
        property.setStatus(status);
        return repository.save(property);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DormitoryRoom> getPropertiesByPriceRange(Double minPrice, Double maxPrice) {
        List<DormitoryRoom> rooms = repository.findByRentBetween(minPrice, maxPrice);

        // Initialize any lazy-loaded collections for each room
        for (DormitoryRoom room : rooms) {
            Hibernate.initialize(room);
        }

        return rooms;
    }
}
