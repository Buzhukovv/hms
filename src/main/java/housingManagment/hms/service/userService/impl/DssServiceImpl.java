package housingManagment.hms.service.userService.impl.DssServiceImpl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.DSS;
import housingManagment.hms.enums.userEnum.DepartmentOfStudentServicesRole;
import housingManagment.hms.repository.userRepository.DepartmentOfStudentServicesRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.DssService;
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
public class DssServiceImpl implements DssService {

    private final DepartmentOfStudentServicesRepository repository;
    private final BaseUserService baseUserService;

    @Override
    public DSS createUser(DSS user) {
        if (user == null) {
            throw new IllegalArgumentException("DSS user cannot be null");
        }
        return (DSS) repository.save(user);
    }

    @Override
    @Transactional
    public DSS updateUser(UUID id, DSS user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and user cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        if (!(baseUser.get() instanceof DSS)) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        DSS existingUser = (DSS) baseUser.get();

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

        return (DSS) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        if (!(baseUser.get() instanceof DSS)) {
            throw new IllegalArgumentException("User with id " + id + " is not a DSS user");
        }

        DSS existingUser = (DSS) baseUser.get();
        repository.delete(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DSS> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isPresent() && baseUser.get() instanceof DSS) {
            return Optional.of((DSS) baseUser.get());
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DSS> findAll() {
        return baseUserService.findAllByType(DSS.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DSS> findDSSByRole(DepartmentOfStudentServicesRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return baseUserService.findAllByType(DSS.class).stream()
                .filter(dss -> dss.getRole() == role)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public long countByRole() {
        return baseUserService.findAllByType(DSS.class).stream()
                .collect(Collectors.groupingBy(DSS::getRole, Collectors.counting()))
                .values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }
}