package housingManagment.hms.dto;

import lombok.Data;

@Data
public class FamilyMemberDTO {
    private String relation;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String localPhone;
}
