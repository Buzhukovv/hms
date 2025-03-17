package housingManagment.hms.repository.propertyRepository;

import housingManagment.hms.entities.property.BaseProperty;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * A concrete implementation of BasePropertyRepository for BaseProperty
 * entities.
 * This is needed because BasePropertyRepository is marked
 * with @NoRepositoryBean
 * which makes it unavailable for direct dependency injection.
 */
@Repository
public interface PropertyRepository extends BasePropertyRepository<BaseProperty> {
}