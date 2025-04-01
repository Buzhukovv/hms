package housingManagment.hms.enums.userEnum;

public enum TeacherPosition {
    PROFESSOR,
    ASSOCIATE_PROFESSOR,
    ASSISTANT_PROFESSOR,
    SENIOR_LECTURER,
    LECTURER,
    TEACHING_ASSISTANT;

    public String getDisplayName() {
        return name().replace("_", " ");
    }
}