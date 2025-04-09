package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
import housingManagment.hms.entities.userEntity.Teacher;
import housingManagment.hms.enums.userEnum.HousingManagementRole;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.HousingManagementRepository;
import housingManagment.hms.service.userService.HousingManagementService;
import housingManagment.hms.service.userService.BaseUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HousingManagementServiceImpl implements HousingManagementService {

    @Autowired
    private HousingManagementRepository housingManagementRepository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public HousingManagement createUser(HousingManagement user) {
        return (HousingManagement) housingManagementRepository.save(user);
    }

    @Override
    @Transactional
    public HousingManagement updateUser(UUID id, HousingManagement user) {
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        HousingManagement existingUser = (HousingManagement) baseUser.get();

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
        existingUser.setBlock(user.getBlock());

        // Save and return the updated user
        return (HousingManagement) housingManagementRepository.save(existingUser);
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

        HousingManagement existingUser = (HousingManagement) baseUser.get();

        // Delete the user
        housingManagementRepository.delete(existingUser);
    }


    @Override
    public List<HousingManagement> findHousingManagementByRole(HousingManagementRole role) {
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(hm -> hm.getRole() == role)
                .collect(Collectors.toList());
    }
}
