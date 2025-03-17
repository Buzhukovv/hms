package housingManagment.hms.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import housingManagment.hms.entities.userEntity.BaseUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saved_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SavedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String reportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BaseUser createdBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reportParameters;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String reportData;

    @CreationTimestamp
    private LocalDateTime createdAt;
}