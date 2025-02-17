package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.DormitoryRoom;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DormitoryRoomRepository extends JpaRepository<DormitoryRoom, UUID> {

    List<DormitoryRoom> findByStatus(PropertyStatus status);

    @Query("SELECT d FROM DormitoryRoom d " +
           "WHERE LOWER(d.propertyBlock) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(d.propertyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DormitoryRoom> searchProperties(@Param("keyword") String keyword);

    @Query("SELECT d FROM DormitoryRoom d WHERE d.status = 'VACANT'")
    List<DormitoryRoom> findAvailableProperties();

    List<DormitoryRoom> findByRentBetween(Double minPrice, Double maxPrice);
}
