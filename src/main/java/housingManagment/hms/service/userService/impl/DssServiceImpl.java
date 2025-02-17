package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.exception.ResourceNotFoundException;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.service.userService.DssService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public DSS createUser(DSS user) {
        return repository.save(user);
    }

    @Override
    public DSS updateUser(UUID id, DSS user) {
        DSS existingUser = getUserById(id);
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
        DSS user = getUserById(id);
        repository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public DSS getUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentOfStudentServices user not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DSS> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DSS> searchUsersByNameOrLastName(String keyword) {
        return repository.findAll().stream()
                .filter(user -> user.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DSS> getUsersByRole(String role) {
        return repository.findAll().stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }
}
