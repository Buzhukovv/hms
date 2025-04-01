package housingManagment.hms.dto;

import housingManagment.hms.enums.MaintenanceRequestStatus;
import housingManagment.hms.enums.MaintenanceRequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequestDTO {

    private UUID id;
    private String requestNumber;

    @NotNull(message = "Requester ID is required")
    private UUID requesterId;

    @NotNull(message = "Lease ID is required")
    private UUID leaseId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private UUID assignedToId;

    private MaintenanceRequestStatus status;

    private MaintenanceRequestType requestType;

    private Boolean isPaid;

    private Double serviceCharge;

    private String notes;

    private UUID propertyId;
    private String propertyNumber;

    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}