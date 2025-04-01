package housingManagment.hms.enums.userEnum.schools;

public enum GraduateSchoolOfPublicPolicy {
    PUBLIC_POLICY,
    PUBLIC_ADMINISTRATION;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "Graduate School of Public Policy";
    }
}