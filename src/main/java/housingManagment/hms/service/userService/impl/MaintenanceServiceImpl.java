package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.MaintenanceRole;
import housingManagment.hms.repository.userRepository.MaintenanceRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.MaintenanceService;
import lombok.RequiredArgsConstructor;
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
    private final BaseUserService baseUserService;

    @Override
    public Maintenance createUser(Maintenance user) {
        if (user == null) {
            throw new IllegalArgumentException("Maintenance user cannot be null");
        }
        return (Maintenance) maintenanceRepository.save(user);
    }

    @Override
    @Transactional
    public Maintenance updateUser(UUID id, Maintenance user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and user cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Maintenance user");
        }

        if (!(baseUser.get() instanceof Maintenance)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Maintenance user");
        }

        Maintenance existingUser = (Maintenance) baseUser.get();

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

        return (Maintenance) maintenanceRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Maintenance user");
        }

        if (!(baseUser.get() instanceof Maintenance)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Maintenance user");
        }

        Maintenance existingUser = (Maintenance) baseUser.get();
        maintenanceRepository.delete(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Maintenance> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isPresent() && baseUser.get() instanceof Maintenance) {
            return Optional.of((Maintenance) baseUser.get());
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        return baseUserService.findAllByType(Maintenance.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> findMaintenanceByRole(MaintenanceRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return baseUserService.findAllByType(Maintenance.class).stream()
                .filter(m -> m.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole() {
        return baseUserService.findAllByType(Maintenance.class).stream()
                .collect(Collectors.groupingBy(Maintenance::getRole, Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }
}