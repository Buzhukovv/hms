package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.service.userService.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BaseUserServiceImpl implements BaseUserService {

    @Autowired
    private BaseUserRepository baseUserRepository;

    @Override
    public Optional<BaseUser> findById(UUID id) {
        return baseUserRepository.findById(id);
    }

    @Override
    public Optional<BaseUser> findByEmail(String email) {
        return baseUserRepository.findByEmail(email);
    }

    @Override
    public Optional<BaseUser> findByNuid(int nuid) {
        return baseUserRepository.findByNuid(nuid);
    }

    @Override
    public Optional<BaseUser> findByNationalId(int nationalId) {
        return baseUserRepository.findByNationalId(nationalId);
    }

    @Override
    public List<BaseUser> findAll() {
        return baseUserRepository.findAll();
    }

    @Override
    public List<BaseUser> findByName(String name) {
        return baseUserRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Override
    public BaseUser save(BaseUser user) {
        return (BaseUser) baseUserRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        baseUserRepository.deleteById(id);
    }

    @Override
    public long count() {
        return baseUserRepository.count();
    }

    @Override
    public <T extends BaseUser> List<T> findAllByType(Class<T> userType) {
        return baseUserRepository.findAllByType(userType)
                .stream()
                .map(userType::cast)
                .toList();
    }

    @Override
    public <T extends BaseUser> long countByType(Class<T> userType) {
        return baseUserRepository.countByType(userType);
    }

    @Override
    public Map<String, Object> countAllTenantTypes() {
        return baseUserRepository.countAllTenantTypes();
    }
}