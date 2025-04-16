package housingManagment.hms.entities.userEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import housingManagment.hms.enums.userEnum.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user_base")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;

    @Column(nullable = false, unique = true)
    private String nationalId;

    @Column(unique = true)
    private String nuid;

    @Column(nullable = false)
    private int identityDocNo;

    @Column(nullable = false)
    private LocalDate identityIssueDate;

    @Column(nullable = false, unique = true)
    private String email;

    private String localPhone;

    @Column(nullable = false)
    private String password;

    private String vehicle;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
