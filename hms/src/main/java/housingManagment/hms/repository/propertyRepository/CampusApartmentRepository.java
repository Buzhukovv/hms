package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.CampusApartment;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampusApartmentRepository extends JpaRepository<CampusApartment, UUID> {

    List<CampusApartment> findByStatus(PropertyStatus status);

    @Query("SELECT c FROM CampusApartment c " +
           "WHERE LOWER(c.propertyBlock) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CampusApartment> searchProperties(@Param("keyword") String keyword);

    @Query("SELECT c FROM CampusApartment c WHERE c.status = 'VACANT'")
    List<CampusApartment> findAvailableProperties();

    List<CampusApartment> findByRentBetween(Double minPrice, Double maxPrice);
}
