package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.DssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


// TODO : Check all of the endpoints and add if business logic needs new endpoints


@Service
@RequiredArgsConstructor
@Transactional
public class DssServiceImpl implements DssService {

    private final DepartmentOfStudentServicesRepository repository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public DSS createUser(DSS user) {
        return (DSS) repository.save(user);
    }

    @Override
    @Transactional
    public DSS updateUser(UUID id, DSS user) {
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        DSS existingUser = (DSS) baseUser.get();

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
        return (DSS) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        // Find the user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        // Verify that the user is a DSS instance
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        DSS existingUser = (DSS) baseUser.get();

        // Delete the user
        repository.delete(existingUser);
    }

    @Override
    public List<DSS> findDSSByRole(DepartmentOfStudentServicesRole role) {
        return baseUserService.findAllByType(DSS.class).stream()
                .filter(dss -> dss.getRole() == role)
                .collect(Collectors.toList());
    }
}
