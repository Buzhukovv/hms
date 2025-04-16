package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.property.CampusApartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BasePropertyRepository<T extends BaseProperty> extends JpaRepository<T, UUID> {

    Optional<T> findByPropertyNumber(String propertyNumber);

    @Query("SELECT p FROM #{#entityName} p WHERE p.propertyNumber LIKE %:keyword% OR p.propertyBlock LIKE %:keyword%")
    Page<T> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) FROM BaseProperty p WHERE TYPE(p) = :propertyType")
    long countByType(@Param("propertyType") Class<? extends BaseProperty> propertyType);


}