package housingManagment.hms.enums.userEnum.schools;

public enum GraduateSchoolOfBusiness {
    BUSINESS_ADMINISTRATION,
    FINANCE;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "Graduate School of Business";
    }
}