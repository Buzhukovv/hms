package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.Cottage;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CottageRepository extends JpaRepository<Cottage, UUID> {

    List<Cottage> findByStatus(PropertyStatus status);

    @Query("SELECT c FROM Cottage c " +
           "WHERE LOWER(c.propertyBlock) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Cottage> searchProperties(@Param("keyword") String keyword);

    @Query("SELECT c FROM Cottage c WHERE c.status = 'VACANT'")
    List<Cottage> findAvailableProperties();

    List<Cottage> findByRentBetween(Double minPrice, Double maxPrice);
}
