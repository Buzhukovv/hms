package housingManagment.hms.enums.property;

import lombok.Getter;

@Getter
public enum PropertyStatus {
    OCCUPIED("Occupied"),
    RESERVED("Reserved"),
    VACANT("Vacant"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    PropertyStatus(String displayName) {
        this.displayName = displayName;
    }

}
