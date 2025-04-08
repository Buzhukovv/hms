package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseUserRepository<BaseUser> {

    /**
     * Count users by type
     */
    @Query("SELECT COUNT(u) FROM BaseUser u WHERE TYPE(u) = :type")
    long countByType(@Param("type") Class<? extends BaseUser> type);

    /**
     * Find all users of a specific type
     */
    @Query("SELECT u FROM BaseUser u WHERE TYPE(u) = :type")
    List<Student> findAllByType(@Param("type") Class<Student> type);

    /**
     * Count all users by type (returns a map of user type to count)
     */
    @Query("SELECT TYPE(u) as userType, COUNT(u) as count " +
            "FROM BaseUser u " +
            "GROUP BY TYPE(u)")
    Map<String, Object> countAllTenantTypes();
}