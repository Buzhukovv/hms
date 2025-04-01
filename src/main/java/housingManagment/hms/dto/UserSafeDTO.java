package housingManagment.hms.dto;

import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.userEnum.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Data Transfer Object for safely displaying user data
 * without revealing sensitive information
 */
@Data
@NoArgsConstructor
public class UserSafeDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String maskedEmail;
    private Gender gender;
    private String maskedPhone;
    private String maskedNationalId;
    private String maskedNuid;
    private String maskedDocNo;
    private String roleType;

    /**
     * Creates a safe DTO from a user entity
     */
    public static UserSafeDTO fromUser(BaseUser user) {
        if (user == null) {
            return null;
        }

        UserSafeDTO dto = new UserSafeDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMaskedEmail(maskEmail(user.getEmail()));
        dto.setGender(user.getGender());
        dto.setMaskedPhone(maskPhone(user.getLocalPhone()));

        // Mask document numbers - these are primitives, so they're always present
        dto.setMaskedNationalId(maskDocument(String.valueOf(user.getNationalId())));
        dto.setMaskedNuid(maskDocument(String.valueOf(user.getNuid())));
        dto.setMaskedDocNo(maskDocument(String.valueOf(user.getIdentityDocNo())));

        return dto;
    }

    /**
     * Masks an email address, showing only the first character and domain
     * Example: j****@example.com
     */
    private static String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email; // Return unchanged if the format is invalid
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        // Keep first character, mask the rest of local part
        return localPart.substring(0, 1) +
                "*".repeat(localPart.length() - 1) +
                domain;
    }

    /**
     * Masks a phone number, showing only the last 4 digits
     * Example: ******1234
     */
    private static String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        if (phone.length() <= 4) {
            return phone; // Return unchanged if too short
        }

        // Show only last 4 digits
        return "*".repeat(phone.length() - 4) +
                phone.substring(phone.length() - 4);
    }

    /**
     * Masks a document number, showing only first and last 2 characters
     * Example: AB****78
     */
    private static String maskDocument(String document) {
        if (document == null || document.isEmpty()) {
            return "";
        }

        if (document.length() <= 4) {
            return document; // Return unchanged if too short
        }

        // Show first 2 and last 2 characters
        return document.substring(0, 2) +
                "*".repeat(document.length() - 4) +
                document.substring(document.length() - 2);
    }
}