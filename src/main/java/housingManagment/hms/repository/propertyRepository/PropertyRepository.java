package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.property.CampusApartment;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.property.PropertyStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PropertyRepository extends BasePropertyRepository<BaseProperty> {

    // Count properties by class belongin
    @Query("SELECT COUNT(p) FROM BaseProperty p WHERE TYPE(p) = :propertyType")
    long countByType(@Param("propertyType") Class<? extends BaseProperty> propertyType);
    

    /**
     * Find all distinct property types
     */
    @Query("SELECT DISTINCT TYPE(p) FROM BaseProperty p")
    List<Class<? extends BaseProperty>> findAllPropertyTypes();

    /**
     * Count all properties by type (returns a map of property type to count)
     */
    @Query("SELECT TYPE(p) as propertyType, COUNT(p) as count " +
            "FROM BaseProperty p " +
            "GROUP BY TYPE(p)")
    Map<String, Object> countAllPropertyTypes();

    /**
     * Find properties by block
     */
    @Query("SELECT p FROM BaseProperty p WHERE p.propertyBlock = :block")
    List<BaseProperty> findByPropertyBlock(@Param("block") String block);

    /**
     * Count properties by status
     */
    @Query("SELECT COUNT(p) FROM BaseProperty p WHERE p.status = :status")
    long countByStatus(@Param("status") PropertyStatus status);
}