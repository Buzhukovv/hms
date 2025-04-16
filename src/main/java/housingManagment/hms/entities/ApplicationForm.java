package housingManagment.hms.entities;

import housingManagment.hms.entities.userEntity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "application_forms")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Boolean sociallyVulnerableCategory;

    @Lob
    @Column(nullable = false)
    private byte[] documentDoNotHavePropertyInCityFile;

    @Column(nullable = false)
    private Boolean documentDoNotHavePropertyInCityValid;

    @Lob
    @Column(nullable = false)
    private byte[] parentWorkPlaceFile;

    @Lob
    @Column(nullable = false)
    private byte[] parentsPlaceResidentsFile;

    @Column(nullable = false)
    private Boolean parentsPlaceResidentsValid;

    @Column(nullable = false)
    private Boolean allChecked;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
