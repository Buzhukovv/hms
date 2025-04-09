package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.repository.userRepository.BaseUserRepository;
import housingManagment.hms.service.userService.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// TODO : Check all of the endpoints and add if business logic needs new endpoints

@Service
public class BaseUserServiceImpl implements BaseUserService {

    @Autowired
    private BaseUserRepository baseUserRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<BaseUser> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return baseUserRepository.findById(id);
    }

    @Override
    public BaseUser findByEmail(String email) {
        return baseUserRepository.findByEmail(email);
    }

    @Override
    public BaseUser findByNuid(int nuid) {
        return baseUserRepository.findByNuid(nuid);
    }

    @Override
    public BaseUser findByNationalId(int nationalId) {
        return baseUserRepository.findByNationalId(nationalId);
    }

    @Override
    public List<BaseUser> findAll() {
        return baseUserRepository.findAll();
    }

    @Override
    public List<BaseUser> findByName(String name) {
        return List.of();
    }

    @Override
    public BaseUser updateUser(UUID id, BaseUser user){

        BaseUser existingUser = user;

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

        // Save and return the updated user
        return (BaseUser) baseUserRepository.save(existingUser);
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
    public <T extends BaseUser> List findAllByType(Class<T> userType) {
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