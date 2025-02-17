package housingManagment.hms.enums.property;

import lombok.Getter;

@Getter
public enum RoomTypeDormitory {
    TWO_BEDDED_ROOM("2-Bedded Room"),
    THREE_BEDDED_ROOM("3-Bedded Room"),
    FOUR_BEDDED_ROOM("4-Bedded Room");

    private final String displayName;

    RoomTypeDormitory(String displayName) {
        this.displayName = displayName;
    }
}
