package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.HousingManagement;
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
    public HousingManagement createUser(HousingManagement user) {return housingManagementRepository.save(user);}

    @Override
    @Transactional
    public HousingManagement updateUser(UUID id, HousingManagement user) {
        // Find the existing user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a HousingManagement instance
        if (!(baseUser instanceof HousingManagement existingUser)) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        // Cast to HousingManagement

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
        return housingManagementRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a HousingManagement instance
        if (!(baseUser instanceof HousingManagement user)) {
            throw new IllegalArgumentException("User with id " + id + " is not a HousingManagement user");
        }

        // Delete the user
        housingManagementRepository.delete(user);
    }


    @Override
    public List<HousingManagement> findHousingManagementByRole(HousingManagementRole role) {
        return baseUserService.findAllByType(HousingManagement.class).stream()
                .filter(hm -> hm.getRole() == role)
                .collect(Collectors.toList());
    }
}
