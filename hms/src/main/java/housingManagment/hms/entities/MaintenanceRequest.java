package housingManagment.hms.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Maintenance;
import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.MaintenanceRequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private BaseUser requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_id", nullable = false)
    private Lease lease;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private Maintenance assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private MaintenanceRequestType requestType;

    @Column(nullable = false)
    private Boolean isPaid;

    private Double serviceCharge;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime completedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (requestNumber == null) {
            // Format: MNT-YYYYMMDD-XXXX (XXXX – random number)
            String dateStr = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
            String random = String.format("%04d", (int) (Math.random() * 10000));
            this.requestNumber = "MNT-" + dateStr + "-" + random;
        }

        if (status == null) {
            this.status = MaintenanceRequestStatus.PENDING;
        }

        if (isPaid == null) {
            this.isPaid = false;
        }
    }
}