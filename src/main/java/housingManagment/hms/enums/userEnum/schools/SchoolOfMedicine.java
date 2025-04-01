package housingManagment.hms.enums.userEnum.schools;

public enum SchoolOfMedicine {
    MEDICINE,
    SURGERY,
    NURSING;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "School of Medicine";
    }
}