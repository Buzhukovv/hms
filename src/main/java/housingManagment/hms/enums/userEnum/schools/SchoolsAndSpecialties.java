package housingManagment.hms.enums.userEnum.schools;

public enum SchoolsAndSpecialties {
    SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES("SCHOOL_OF_ENGINEERING_AND_DIGITAL_SCIENCES"),
    SCHOOL_OF_MEDICINE("SCHOOL_OF_MEDICINE"),
    SCHOOL_OF_SCIENCES_AND_HUMANITIES("SCHOOL_OF_SCIENCES_AND_HUMANITIES"),
    GRADUATE_SCHOOL_OF_PUBLIC_POLICY("GRADUATE_SCHOOL_OF_PUBLIC_POLICY"),
    GRADUATE_SCHOOL_OF_BUSINESS("GRADUATE_SCHOOL_OF_BUSINESS"),
    GRADUATE_SCHOOL_OF_EDUCATION("GRADUATE_SCHOOL_OF_EDUCATION");

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