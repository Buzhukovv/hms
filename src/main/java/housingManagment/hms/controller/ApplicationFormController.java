package housingManagment.hms.controller;

import housingManagment.hms.entities.ApplicationForm;
import housingManagment.hms.service.ApplicationFormService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
public class ApplicationFormController {

    private final ApplicationFormService applicationFormService;

    public ApplicationFormController(ApplicationFormService applicationFormService) {
        this.applicationFormService = applicationFormService;
    }

    /**
     * Создание новой формы заявления.
     *
     * @param applicationForm объект формы заявления
     * @return созданный объект формы заявления
     */
    @PostMapping
    public ResponseEntity<ApplicationForm> createApplicationForm(@RequestBody ApplicationForm applicationForm) {
        ApplicationForm savedForm = applicationFormService.save(applicationForm);
        return new ResponseEntity<>(savedForm, HttpStatus.CREATED);
    }

    /**
     * Получение списка всех форм заявлений.
     *
     * @return список форм заявлений
     */
    @GetMapping
    public ResponseEntity<List<ApplicationForm>> getAllApplicationForms() {
        List<ApplicationForm> forms = applicationFormService.getAllApplicationForms();
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }

    /**
     * Получение формы заявления по идентификатору.
     *
     * @param id идентификатор формы заявления
     * @return найденная форма заявления
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationForm> getApplicationFormById(@PathVariable UUID id) {
        ApplicationForm form = applicationFormService.getApplicationFormById(id);
        return new ResponseEntity<>(form, HttpStatus.OK);
    }

    /**
     * Обновление существующей формы заявления.
     *
     * @param id идентификатор формы для обновления
     * @param applicationForm обновлённые данные формы заявления
     * @return обновлённая форма заявления
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationForm> updateApplicationForm(@PathVariable UUID id,
                                                                 @RequestBody ApplicationForm applicationForm) {
        ApplicationForm updatedForm = applicationFormService.updateApplicationForm(id, applicationForm);
        return new ResponseEntity<>(updatedForm, HttpStatus.OK);
    }

    /**
     * Удаление формы заявления по идентификатору.
     *
     * @param id идентификатор формы для удаления
     * @return статус операции
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplicationForm(@PathVariable UUID id) {
        applicationFormService.deleteApplicationForm(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
