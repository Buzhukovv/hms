package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BaseUserRepository<S extends BaseUser> extends JpaRepository<BaseUser, UUID> {

        Optional<BaseUser> findByEmail(String email);

        Optional<BaseUser> findByNuid(int nuid);

        Optional<BaseUser> findByNationalId(int nationalId);

        List<BaseUser> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

        long count();

        /**
         * Find all users of a specific type
         */
        @Query("SELECT u FROM BaseUser u WHERE TYPE(u) = :userType")
        <T extends BaseUser> List<T> findAllByType(@Param("userType") Class<T> userType);

        /**
         * Count users by type
         */
        @Query("SELECT COUNT(u) FROM BaseUser u WHERE TYPE(u) = :userType")
        long countByType(@Param("userType") Class<? extends BaseUser> userType);

        /**
         * Count all users by type (returns a map of user type to count)
         */
        @Query("SELECT TYPE(u) as userType, COUNT(u) as count " +
                "FROM BaseUser u " +
                "GROUP BY TYPE(u)")
        Map<String, Object> countAllTenantTypes();
}