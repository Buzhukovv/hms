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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
            String random = String.format("%04d", (int) (Math.random() * 10000));
            this.propertyNumber = "PROP-" + random;
        }
    }
}
