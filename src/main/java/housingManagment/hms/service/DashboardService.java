package housingManagment.hms.service;

import housingManagment.hms.dto.DashboardData;
import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.userEntity.BaseUser;

import java.util.List;

public interface DashboardService {

    long getActiveLeaseCount();

    long getTotalLeaseCount();

    List<Lease> getStudentLeases(BaseUser student);

    List<Lease> getTeacherLeases(BaseUser teacher);

    List<Lease> getLeasesByBlock(String block);

    DashboardData getDashboardData(BaseUser user);
}