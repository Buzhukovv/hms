package housingManagment.hms.enums.userEnum.schools;

public enum SchoolOfEngineeringAndDigitalSciences {
    COMPUTER_SCIENCE,
    ELECTRICAL_ENGINEERING,
    MECHANICAL_ENGINEERING,
    CIVIL_ENGINEERING,
    CHEMICAL_ENGINEERING;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "School of Engineering and Digital Sciences";
    }
}