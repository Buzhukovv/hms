package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.DssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DssServiceImpl implements DssService {

    private final DepartmentOfStudentServicesRepository repository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public DSS createUser(DSS user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public DSS updateUser(UUID id, DSS user) {
        // Find the existing user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a DSS instance
        if (!(baseUser instanceof DSS existingUser)) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

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
        return repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        BaseUser baseUser = baseUserService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify that the user is a DSS instance
        if (!(baseUser instanceof DSS user)) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        // Delete the user
        repository.delete(user);
    }

    @Override
    public List<DSS> findDSSByRole(DepartmentOfStudentServicesRole role) {
        return baseUserService.findAllByType(DSS.class).stream()
                .filter(dss -> dss.getRole() == role)
                .collect(Collectors.toList());
    }
}
