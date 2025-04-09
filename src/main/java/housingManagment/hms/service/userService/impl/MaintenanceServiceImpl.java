package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Maintenance;

import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    @Override
    public Maintenance createUser(Maintenance user) {
        return (Maintenance) maintenanceRepository.save(user);
    }

    @Autowired
    private BaseUserService baseUserService;

    @Override
    @Transactional
    public Maintenance updateUser(UUID id, Maintenance user) {
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        Maintenance existingUser = (Maintenance) baseUser.get();


        // Update fields
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

        // Save and return the updated user
        return (Maintenance) maintenanceRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        Maintenance existingUser = (Maintenance) baseUser.get();

        // Delete the user
        maintenanceRepository.delete(existingUser);
    }

    @Override
    public List<Maintenance> findMaintenanceByRole(MaintenanceRole role) {
        return baseUserService.findAllByType(Maintenance.class).stream()
                .filter(m -> m.getRole() == role)
                .collect(Collectors.toList());
    }
}
