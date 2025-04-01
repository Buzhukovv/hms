package housingManagment.hms.entities.property;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import housingManagment.hms.enums.property.PropertyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "property_base")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "property_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class BaseProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String propertyNumber;

    @Column(nullable = false)
    private Double rent;

    @Column(nullable = false)
    private String propertyBlock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column(nullable = false)
    private Integer maxOccupant;

    /**
     * Сумма депозита, которую необходимо учитывать при заключении аренды.
     */
    @Column
    private Double depositAmount;

    @PrePersist
    public void prePersist() {
        if (propertyNumber == null || propertyNumber.isEmpty()) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String random = String.format("%04d", (int) (Math.random() * 10000));
            this.propertyNumber = "PROP-" + timestamp.substring(timestamp.length() - 4) + "-" + random;
        }
    }

    /**
     * Get the area of the property, delegating to the specific subclass
     * implementation.
     * This is needed for backward compatibility after removing area from
     * BaseProperty.
     * 
     * @return the area of the property from the appropriate subclass, or null if
     *         not applicable
     */
    public Double getArea() {
        if (this instanceof DormitoryRoom) {
            return ((DormitoryRoom) this).getArea();
        } else if (this instanceof Cottage) {
            return ((Cottage) this).getArea();
        } else if (this instanceof CampusApartment) {
            return ((CampusApartment) this).getArea();
        } else if (this instanceof OffCampusApartment) {
            return ((OffCampusApartment) this).getArea();
        } else if (this instanceof Townhouse) {
            return ((Townhouse) this).getArea();
        }
        return 0.0; // Default value if not a recognized subclass
    }
}
