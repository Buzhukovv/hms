package housingManagment.hms.service.userService;

import housingManagment.hms.entities.userEntity.BaseUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BaseUserService {

    Optional<BaseUser> findById(UUID id);
    BaseUser findByEmail(String email);

    BaseUser findByNuid(int nuid);

    BaseUser findByNationalId(int nationalId);

    List<BaseUser> findAll();

    List<BaseUser> findByName(String name);

    BaseUser save(BaseUser user);

    void deleteById(UUID id);

    long count();


    <T extends BaseUser> List<T> findAllByType(Class<T> userType);

    <T extends BaseUser> long countByType(Class<T> userType);

}