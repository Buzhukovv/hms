package housingManagment.hms.service.impl;

import housingManagment.hms.entities.ApplicationForm;
import housingManagment.hms.exception.EntityNotFoundException;
import housingManagment.hms.repository.ApplicationFormRepository;
import housingManagment.hms.service.ApplicationFormService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;

    public ApplicationFormServiceImpl(ApplicationFormRepository applicationFormRepository) {
        this.applicationFormRepository = applicationFormRepository;
    }

    @Override
    public ApplicationForm save(ApplicationForm applicationForm) {
        return applicationFormRepository.save(applicationForm);
    }

    @Override
    public List<ApplicationForm> getAllApplicationForms() {
        return applicationFormRepository.findAll();
    }

    @Override
    public ApplicationForm getApplicationFormById(UUID id) {
        return applicationFormRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ApplicationForm not found with id: " + id));
    }

    @Override
    public ApplicationForm updateApplicationForm(UUID id, ApplicationForm applicationForm) {
        // Сначала проверяем, существует ли форма с данным id
        ApplicationForm existingForm = getApplicationFormById(id);
        // Обновляем поля. Пример обновления:
        existingForm.setSociallyVulnerableCategory(applicationForm.getSociallyVulnerableCategory());
        existingForm.setDocumentDoNotHavePropertyInCityFile(applicationForm.getDocumentDoNotHavePropertyInCityFile());
        existingForm.setDocumentDoNotHavePropertyInCityValid(applicationForm.getDocumentDoNotHavePropertyInCityValid());
        existingForm.setParentWorkPlaceFile(applicationForm.getParentWorkPlaceFile());
        existingForm.setParentsPlaceResidentsFile(applicationForm.getParentsPlaceResidentsFile());
        existingForm.setParentsPlaceResidentsValid(applicationForm.getParentsPlaceResidentsValid());
        existingForm.setAllChecked(applicationForm.getAllChecked());
        // При необходимости, можно обновить и связанные сущности

        return applicationFormRepository.save(existingForm);
    }

    @Override
    public void deleteApplicationForm(UUID id) {
        ApplicationForm existingForm = getApplicationFormById(id);
        applicationFormRepository.delete(existingForm);
    }
}
