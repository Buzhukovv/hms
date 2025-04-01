package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseUserRepository<T extends BaseUser> extends JpaRepository<T, UUID> {

        Optional<T> findByEmail(String email);

        /**
         * Count all users
         */
        @Query("SELECT COUNT(u) FROM #{#entityName} u")
        long countTenants();

        /**
         * Count users by type
         */
        @Query("SELECT TYPE(u) as userType, COUNT(u) as count " +
                        "FROM #{#entityName} u " +
                        "GROUP BY TYPE(u)")
        Map<String, Object> countAllTenantTypes();

        /**
         * Update a user's password by ID
         * 
         * @param id       the user's ID
         * @param password the new encoded password
         */
        @Query("UPDATE #{#entityName} u SET u.password = :password WHERE u.id = :id")
        void updatePassword(@Param("id") UUID id, @Param("password") String password);
}
