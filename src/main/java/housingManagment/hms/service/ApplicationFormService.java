package housingManagment.hms.service;

import housingManagment.hms.entities.ApplicationForm;

import java.util.List;
import java.util.UUID;

public interface ApplicationFormService {

    /**
     * Сохраняет новую форму заявления.
     *
     * @param applicationForm объект формы заявления
     * @return сохранённая форма заявления
     */
    ApplicationForm save(ApplicationForm applicationForm);

    /**
     * Возвращает список всех форм заявлений.
     *
     * @return список форм заявлений
     */
    List<ApplicationForm> getAllApplicationForms();

    /**
     * Возвращает форму заявления по её идентификатору.
     *
     * @param id идентификатор формы
     * @return форма заявления
     */
    ApplicationForm getApplicationFormById(UUID id);

    /**
     * Обновляет форму заявления с указанным идентификатором.
     *
     * @param id идентификатор формы для обновления
     * @param applicationForm обновлённые данные формы заявления
     * @return обновлённая форма заявления
     */
    ApplicationForm updateApplicationForm(UUID id, ApplicationForm applicationForm);

    /**
     * Удаляет форму заявления по идентификатору.
     *
     * @param id идентификатор формы для удаления
     */
    void deleteApplicationForm(UUID id);
}
