package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.HousingManagementService;
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
public class HousingManagementServiceImpl implements HousingManagementService {

    private final HousingManagementRepository housingManagementRepository;
    private final BaseUserService baseUserService;

    @Override
    public HousingManagement createUser(HousingManagement user) {
        if (user == null) {
            throw new IllegalArgumentException("HousingManagement user cannot be null");
        }
        return (HousingManagement) housingManagementRepository.save(user);
    }

    @Override
    @Transactional
    public HousingManagement updateUser(UUID id, HousingManagement user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and user cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        if (!(baseUser.get() instanceof HousingManagement)) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        HousingManagement existingUser = (HousingManagement) baseUser.get();

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

        return (HousingManagement) housingManagementRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        if (!(baseUser.get() instanceof HousingManagement)) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        HousingManagement existingUser = (HousingManagement) baseUser.get();
        housingManagementRepository.delete(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HousingManagement> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isPresent() && baseUser.get() instanceof HousingManagement) {
            return Optional.of((HousingManagement) baseUser.get());
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> findAll() {
        return baseUserService.findAllByType(HousingManagement.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> findHousingManagementByRole(HousingManagementRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(hm -> hm.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingManagement> findByBlock(String block) {
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block cannot be null or empty");
        }
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(hm -> block.equals(hm.getBlock()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignBlock(UUID id, String block) {
        if (id == null || block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("ID and block cannot be null or empty");
        }
        Optional<HousingManagement> housingManagement = findById(id);
        if (housingManagement.isEmpty()) {
            throw new IllegalArgumentException("HousingManagement user with id " + id + " not found");
        }
        HousingManagement existingUser = housingManagement.get();
        existingUser.setBlock(block);
        housingManagementRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllMember() {
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .collect(Collectors.groupingBy(HousingManagement::getRole, Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole(HousingManagementRole role){
        if(role == null){
            throw new IllegalArgumentException();
        }
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(housingManagement -> housingManagement.getRole() == role)
                .count();
    }


    @Override
    @Transactional(readOnly = true)
    public long countByBlock(String block) {
        if (block == null || block.trim().isEmpty()) {
            throw new IllegalArgumentException("Block cannot be null or empty");
        }

        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(hm -> block.equals(hm.getBlock()))
                .count();
    }
}