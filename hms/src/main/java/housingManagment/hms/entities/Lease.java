package housingManagment.hms.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.enums.LeaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "leases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String leaseNumber;

    // При сериализации не включаем поле property, чтобы избежать циклической ссылки
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private BaseProperty property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private BaseUser tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaseStatus status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = true)
    private LocalDate checkOutDate;

    private LocalDate terminationDate;
    private LocalDate renewalDate;

    @Column(nullable = false)
    private Double monthlyRent;

    @Column(nullable = false)
    private Double securityDeposit;

    @Column(nullable = false)
    private Integer leaseTerm;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(unique = true)
    private String contractNumber;

    @Column(nullable = false)
    private Boolean reservationStatus;

    private Double penalties;

    /**
     * Сумма депозита по договору аренды.
     * Если недвижимость не является DormitoryRoom и значение не установлено, оно
     * будет заполнено из property.depositAmount.
     */
    @Column(nullable = false)
    private Double deposit;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (leaseNumber == null) {
            // Формат: LSE-YYYYMMDD-XXXX (XXXX – случайное число)
            String dateStr = LocalDate.now().toString().replace("-", "");
            String random = String.format("%04d", (int) (Math.random() * 10000));
            this.leaseNumber = "LSE-" + dateStr + "-" + random;
        }
    }

    @PreUpdate
    private void validateLeaseRules() {
        if (checkOutDate != null && checkInDate != null && checkOutDate.isBefore(checkInDate)) {
            throw new IllegalStateException("Check-out date must be after check-in date");
        }
        if ((deposit == null || deposit == 0.0) && property.getDepositAmount() != null) {
            this.deposit = property.getDepositAmount();
        }
    }
}
