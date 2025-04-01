package housingManagment.hms.enums.userEnum.schools;

public enum SchoolsAndSpecialties {
    SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES("School of Engineering and Digital Sciences"),
    SCHOOL_OF_MEDICINE("School of Medicine"),
    SCHOOL_OF_SCIENCES_AND_HUMANITIES("School of Sciences and Humanities"),
    GRADUATE_SCHOOL_OF_PUBLIC_POLICY("Graduate School of Public Policy"),
    GRADUATE_SCHOOL_OF_BUSINESS("Graduate School of Business"),
    GRADUATE_SCHOOL_OF_EDUCATION("Graduate School of Education");

    private final String displayName;

    SchoolsAndSpecialties(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SchoolsAndSpecialties fromDisplayName(String displayName) {
        for (SchoolsAndSpecialties school : values()) {
            if (school.displayName.equalsIgnoreCase(displayName)) {
                return school;
            }
        }
        throw new IllegalArgumentException("No school found for display name: " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}