package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.service.userService.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository repository;

    @Override
    public Maintenance createUser(Maintenance user) {
        return repository.save(user);
    }

    @Override
    public Maintenance updateUser(UUID id, Maintenance user) {
        Maintenance existingUser = getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMiddleName(user.getMiddleName());
        existingUser.setNationalId(user.getNationalId());
        existingUser.setNuid(user.getNuid());
        existingUser.setIdentityDocNo(user.getIdentityDocNo());
        existingUser.setIdentityIssueDate(user.getIdentityIssueDate());
        existingUser.setEmail(user.getEmail());
        existingUser.setLocalPhone(user.getLocalPhone());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        Maintenance user = getUserById(id);
        repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Maintenance getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance user not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> searchUsersByNameOrLastName(String keyword) {
        return repository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> getUsersByRole(String role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }
}
