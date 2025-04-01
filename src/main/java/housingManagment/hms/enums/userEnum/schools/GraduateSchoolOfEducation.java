package housingManagment.hms.enums.userEnum.schools;

public enum GraduateSchoolOfEducation {
    EDUCATION,
    LEADERSHIP;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "Graduate School of Education";
    }
}