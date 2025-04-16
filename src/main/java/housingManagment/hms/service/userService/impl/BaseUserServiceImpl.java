package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.service.userService.BaseUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BaseUserServiceImpl implements BaseUserService {

    private final BaseUserRepository baseUserRepository;

    public BaseUserServiceImpl(BaseUserRepository baseUserRepository) {
        this.baseUserRepository = baseUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BaseUser> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return baseUserRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseUser findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return baseUserRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseUser findByNuid(String nuid) {
        return baseUserRepository.findByNuid(nuid);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseUser findByNationalId(String nationalId) {
        return baseUserRepository.findByNationalId(nationalId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseUser> findAll() {
        return baseUserRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseUser> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return baseUserRepository.findAll().stream()
                .filter(user -> (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public BaseUser save(BaseUser user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return baseUserRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        baseUserRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return baseUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseUser> List<T> findAllByType(Class<T> userType) {
        if (userType == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }
        return baseUserRepository.findAllByType(userType)
                .stream()
                .map(userType::cast)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends BaseUser> long countByType(Class<T> userType) {
        if (userType == null) {
            throw new IllegalArgumentException("User type cannot be null");
        }
        return baseUserRepository.countByType(userType);
    }


}