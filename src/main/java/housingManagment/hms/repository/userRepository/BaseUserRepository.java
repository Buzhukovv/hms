package housingManagment.hms.repository.userRepository;

import housingManagment.hms.entities.userEntity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseUserRepository<T extends BaseUser> extends JpaRepository<T, UUID> {

        Optional<T> findByEmail(String email);

        /**
         * Count all users of the specific type T
         */
        @Query("SELECT COUNT(u) FROM #{#entityName} u")
        long countTenants();

        /**
         * Update a user's password by ID
         */
        @Query("UPDATE #{#entityName} u SET u.password = :password WHERE u.id = :id")
        void updatePassword(@Param("id") UUID id, @Param("password") String password);
}