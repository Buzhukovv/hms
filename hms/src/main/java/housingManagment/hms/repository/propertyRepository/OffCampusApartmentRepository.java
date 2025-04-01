package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.OffCampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OffCampusApartmentRepository extends JpaRepository<OffCampusApartment, UUID> {

    List<OffCampusApartment> findByStatus(PropertyStatus status);

    @Query("SELECT o FROM OffCampusApartment o " +
           "WHERE LOWER(o.propertyBlock) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(o.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<OffCampusApartment> searchProperties(@Param("keyword") String keyword);

    @Query("SELECT o FROM OffCampusApartment o WHERE o.status = 'VACANT'")
    List<OffCampusApartment> findAvailableProperties();

    List<OffCampusApartment> findByRentBetween(Double minPrice, Double maxPrice);
}
