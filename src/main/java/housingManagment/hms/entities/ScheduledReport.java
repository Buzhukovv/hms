package housingManagment.hms.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import housingManagment.hms.entities.userEntity.BaseUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "scheduled_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ScheduledReport {

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

    @Column(nullable = false)
    private String schedule; // DAILY, WEEKLY, MONTHLY, QUARTERLY, SEMESTER

    @Column(nullable = false)
    private String cronExpression;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reportParameters;

    @ElementCollection
    @CollectionTable(name = "scheduled_report_recipients", joinColumns = @JoinColumn(name = "scheduled_report_id"))
    @Column(name = "email")
    private List<String> recipients = new ArrayList<>();

    @Column(nullable = false)
    private String reportFormat; // PDF, EXCEL, CSV

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime lastRunAt;

    private LocalDateTime nextRunAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}