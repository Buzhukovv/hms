package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.Townhouse;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TownhouseRepository extends JpaRepository<Townhouse, UUID> {

    List<Townhouse> findByStatus(PropertyStatus status);

    @Query("SELECT c FROM Townhouse c " +
            "WHERE LOWER(c.propertyBlock) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Townhouse> searchProperties(@Param("keyword") String keyword);

    @Query("SELECT c FROM Townhouse c WHERE c.status = 'VACANT'")
    List<Townhouse> findAvailableProperties();

    List<Townhouse> findByRentBetween(Double minPrice, Double maxPrice);
}
