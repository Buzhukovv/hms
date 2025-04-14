package housingManagment.hms.service.userService.impl;

import housingManagment.hms.entities.Lease;
import housingManagment.hms.entities.property.BaseProperty;
import housingManagment.hms.entities.userEntity.BaseUser;
import housingManagment.hms.entities.userEntity.Student;
import housingManagment.hms.enums.userEnum.StudentRole;
import housingManagment.hms.enums.userEnum.schools.SchoolsAndSpecialties;
import housingManagment.hms.repository.LeaseRepository;
import housingManagment.hms.repository.userRepository.StudentRepository;
import housingManagment.hms.service.userService.BaseUserService;
import housingManagment.hms.service.userService.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final LeaseRepository leaseRepository;

    @Autowired
    private BaseUserService baseUserService;

    @Override
    public Student createUser(Student user) {
        if (user == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        return (Student) repository.save(user);
    }

    @Override
    @Transactional
    public Student updateUser(UUID id, Student user) {
        if (id == null || user == null) {
            throw new IllegalArgumentException("ID and user cannot be null");
        }
        // Find the existing user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student user");
        }

        if (!(baseUser.get() instanceof Student)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student");
        }

        Student existingUser = (Student) baseUser.get();

        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMiddleName(user.getMiddleName());
        existingUser.setNationalId(user.getNationalId());
        existingUser.setNuid(user.getNuid());
        existingUser.setIdentityDocNo(user.getIdentityDocNo());
        existingUser.setIdentityIssueDate(user.getIdentityIssueDate());
        existingUser.setEmail(user.getEmail());
        existingUser.setLocalPhone(user.getLocalPhone());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        existingUser.setSchool(user.getSchool());
        existingUser.setSpecialty(user.getSpecialty());

        // Save and return the updated user
        return (Student) repository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        // Find the user using BaseUserService
        Optional<BaseUser> baseUser = baseUserService.findById(id);

        if (baseUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student user");
        }

        if (!(baseUser.get() instanceof Student)) {
            throw new IllegalArgumentException("User with id " + id + " is not a Student");
        }

        Student existingUser = (Student) baseUser.get();

        // Delete the user
        repository.delete(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<BaseUser> baseUser = baseUserService.findById(id);
        if (baseUser.isPresent() && baseUser.get() instanceof Student) {
            return Optional.of((Student) baseUser.get());
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return baseUserService.findAllByType(Student.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findStudentsByRole(StudentRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return baseUserService.findAllByType(Student.class).stream()
                .filter(s -> s.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getRoommates(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Student> student = findById(id);
        if (student.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }

        // Find the student's current lease
        List<Lease> leases = leaseRepository.findByTenantId(id);
        List<Student> roommates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Lease lease : leases) {
            if (lease.getStartDate().isBefore(today) && (lease.getEndDate() == null || lease.getEndDate().isAfter(today))) {
                BaseProperty property = lease.getProperty();
                if (property != null) {
                    // Find all leases for the same property that are currently active
                    List<Lease> propertyLeases = leaseRepository.findByPropertyId(property.getId());
                    for (Lease otherLease : propertyLeases) {
                        if (!otherLease.getTenant().getId().equals(id) &&
                                otherLease.getStartDate().isBefore(today) &&
                                (otherLease.getEndDate() == null || otherLease.getEndDate().isAfter(today))) {
                            Optional<Student> roommate = findById(otherLease.getTenant().getId());
                            roommate.ifPresent(roommates::add);
                        }
                    }
                }
            }
        }
        return roommates;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getExRoommates(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        Optional<Student> student = findById(id);
        if (student.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }

        // Find the student's past leases
        List<Lease> leases = leaseRepository.findByTenantId(id);
        List<Student> exRoommates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Lease lease : leases) {
            if (lease.getEndDate() != null && lease.getEndDate().isBefore(today)) {
                BaseProperty property = lease.getProperty();
                if (property != null) {
                    // Find all leases for the same property that overlap with the student's lease
                    List<Lease> propertyLeases = leaseRepository.findByPropertyId(property.getId());
                    for (Lease otherLease : propertyLeases) {
                        if (!otherLease.getTenant().getId().equals(id) && otherLease.getEndDate() != null && otherLease.getEndDate().isBefore(today) && otherLease.getStartDate().isBefore(lease.getEndDate()) && otherLease.getEndDate().isAfter(lease.getStartDate())) {
                            Optional<Student> exRoommate = findById(otherLease.getTenant().getId());
                            exRoommate.ifPresent(rm -> {
                                if (!exRoommates.contains(rm)) {
                                    exRoommates.add(rm);
                                }
                            });
                        }
                    }
                }
            }
        }
        return exRoommates;
    }

    @Override
    @Transactional
    public Student changeRole(UUID id, StudentRole role) {
        if (id == null || role == null) {
            throw new IllegalArgumentException("ID and role cannot be null");
        }
        Optional<Student> student = findById(id);
        if (student.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }
        Student existingStudent = student.get();
        existingStudent.setRole(role);
        return (Student) repository.save(existingStudent);
    }

    @Override
    @Transactional
    public Student changeSchool(UUID id, SchoolsAndSpecialties school) {
        if (id == null || school == null) {
            throw new IllegalArgumentException("ID and school cannot be null");
        }
        Optional<Student> student = findById(id);
        if (student.isEmpty()) {
            throw new IllegalArgumentException("Student with id " + id + " not found");
        }
        Student existingStudent = student.get();
        existingStudent.setSchool(school);
        return (Student) repository.save(existingStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole(StudentRole role) {
        if(role == null){
            throw new IllegalArgumentException();
        }
        return baseUserService.findAllByType(Student.class).stream()
                .filter(student -> student.getRole() == role)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySchool(SchoolsAndSpecialties school) {
        return baseUserService.findAllByType(Student.class).stream()
                .filter(student -> school.equals(student.getSchool()))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countBySpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be null or empty");
        }
        return baseUserService.findAllByType(Student.class).stream()
                .filter(student -> specialty.equalsIgnoreCase(student.getSpecialty()))
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySchool(SchoolsAndSpecialties school) {
        if (school == null) {
            throw new IllegalArgumentException("School cannot be null");
        }

        return repository.findBySchool(school);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getUsersBySpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty cannot be null or empty");
        }
        return repository.findBySpecialty(specialty);
    }
}