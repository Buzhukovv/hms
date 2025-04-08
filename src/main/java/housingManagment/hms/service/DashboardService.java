package housingManagment.hms.service;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.entities.userEntity.BaseUser;

public interface DashboardService {
    DashboardData getDashboardData(BaseUser user);
}