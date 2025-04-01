package housingManagment.hms.repository.propertyRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import housingManagment.hms.entities.property.BaseProperty;

@NoRepositoryBean
public interface BasePropertyRepository<T extends BaseProperty> extends JpaRepository<T, UUID> {

    Optional<T> findByPropertyNumber(String propertyNumber);

    @Query("SELECT p FROM #{#entityName} p WHERE p.propertyNumber LIKE %:keyword% OR p.propertyBlock LIKE %:keyword%")
    Page<T> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT TYPE(p) FROM BaseProperty p")
    List<Class<? extends BaseProperty>> findAllPropertyTypes();

    @Query("SELECT COUNT(p) FROM BaseProperty p WHERE TYPE(p) = :type")
    long countByType(@Param("type") Class<? extends BaseProperty> type);
}
