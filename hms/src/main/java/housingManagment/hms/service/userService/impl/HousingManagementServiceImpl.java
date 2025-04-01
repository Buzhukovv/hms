package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.service.userService.HousingManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HousingManagementServiceImpl implements HousingManagementService {

    private final HousingManagementRepository repository;

    @Override
    public HousingManagement createUser(HousingManagement user) {return repository.save(user);}

    @Override
    public HousingManagement updateUser(UUID id, HousingManagement user) {
        HousingManagement existingUser = getUserById(id);
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
        existingUser.setBlock(user.getBlock());
        return repository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        HousingManagement user = getUserById(id);
        repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public HousingManagement getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HousingManagement user not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> searchUsersByNameOrLastName(String keyword) {
        return repository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> getUsersByRole(String role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }
}
