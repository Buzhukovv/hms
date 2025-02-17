package housingManagment.hms.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class LeaseCreateDTO {
    private String leaseNumber;
    private UUID propertyId;
    private UUID tenantId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double monthlyRent;
    private Double securityDeposit;
    private Integer leaseTerm;
    private String terms;
    private String notes;
    private String contractNumber;
    private Boolean reservationStatus;
    private Double penalties;
    private Double deposit;

    private List<FamilyMemberDTO> familyMembers;

}
