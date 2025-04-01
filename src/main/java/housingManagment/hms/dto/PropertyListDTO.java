package housingManagment.hms.dto;

import housingManagment.hms.enums.property.PropertyStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PropertyListDTO {
    private UUID id;
    private String propertyNumber;
    private String propertyBlock;
    private String propertyType; // DormitoryRoom, Cottage, OffCampusApartment, etc.
    private String specificType; // Room type for dorms, apartment type for off-campus
    private Integer maxOccupant;
    private Double rent;
    private Double depositAmount;
    private Double area;
    private PropertyStatus status;
    private Boolean isPaid;
    private String currentOccupants; // Number of current occupants / max occupants
    private String address; // For off-campus properties
}