package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * A concrete implementation of BaseUserRepository for BaseUser entities.
 * This is needed because BaseUserRepository is marked with @NoRepositoryBean
 * which makes it unavailable for direct dependency injection.
 */
@Repository
public interface UserRepository extends BaseUserRepository<BaseUser> {
}