package housingManagment.hms.enums.userEnum.schools;

public enum SchoolOfSciencesAndHumanities {
    HISTORY,
    LITERATURE,
    POLITICAL_SCIENCE,
    SOCIOLOGY,
    MATHEMATICS,
    PHYSICS,
    CHEMISTRY,
    BIOLOGY,
    PHILOSOPHY,
    LINGUISTICS,
    ECONOMICS;

    public String getDisplayName() {
        return name().replace("_", " ");
    }

    public String getSchoolName() {
        return "School of Sciences and Humanities";
    }
}