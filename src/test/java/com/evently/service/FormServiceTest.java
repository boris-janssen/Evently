package com.evently.service;

import com.evently.dto.EntryRequest;
import com.evently.dto.EntryResponse;
import com.evently.dto.FormFieldRequest;
import com.evently.dto.FormRequest;
import com.evently.dto.FormResponse;
import com.evently.exception.EntryValidationException;
import com.evently.exception.FieldError;
import com.evently.exception.FormNotFoundException;
import com.evently.model.FieldType;
import com.evently.model.Form;
import com.evently.model.FormEntry;
import com.evently.model.FormField;
import com.evently.repository.FormEntryRepository;
import com.evently.repository.FormRepository;
import com.evently.validation.EntryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormServiceTest {

    private static final String FORM_ID          = "form-123";
    private static final String FORM_TITLE       = "Spring Meetup";
    private static final String FORM_DESCRIPTION = "A fun meetup";
    private static final LocalDateTime FORM_CREATED_AT = LocalDateTime.of(2026, 6, 1, 10, 0);

    @Mock
    private FormRepository formRepository;

    @Mock
    private FormEntryRepository formEntryRepository;

    @Mock
    private EntryValidator entryValidator;

    @InjectMocks
    private FormService formService;

    private static final String ENTRY_ID = "entry-456";
    private static final LocalDateTime ENTRY_SUBMITTED_AT = LocalDateTime.of(2026, 6, 2, 9, 0);

    private Form savedForm;
    private FormEntry savedEntry;

    @BeforeEach
    void setUp() {
        savedForm = new Form(FORM_TITLE, FORM_DESCRIPTION, List.of(
                new FormField("name", "Full Name", FieldType.TEXT, true),
                new FormField("email", "Email", FieldType.EMAIL, true)
        ), FORM_CREATED_AT);
        savedForm.setId(FORM_ID);

        savedEntry = new FormEntry(FORM_ID, Map.of("name", "Jack Bauer", "email", "jack@example.com"), ENTRY_SUBMITTED_AT);
        savedEntry.setEntryId(ENTRY_ID);
    }

    // --- createForm ---

    @Test
    void createForm_savesFormAndReturnsResponse() {
        when(formRepository.save(any(Form.class))).thenReturn(savedForm);

        FormResponse response = formService.createForm(buildRequest());

        verify(formRepository).save(any(Form.class));
        assertThat(response.getId()).isEqualTo(FORM_ID);
        assertThat(response.getTitle()).isEqualTo(FORM_TITLE);
        assertThat(response.getDescription()).isEqualTo(FORM_DESCRIPTION);
        assertThat(response.getCreatedAt()).isEqualTo(FORM_CREATED_AT);
    }

    @Test
    void createForm_mapsFieldsCorrectly() {
        when(formRepository.save(any(Form.class))).thenReturn(savedForm);

        FormResponse response = formService.createForm(buildRequest());

        assertThat(response.getFields()).hasSize(2);
        assertThat(response.getFields().get(0).getName()).isEqualTo("name");
        assertThat(response.getFields().get(0).getLabel()).isEqualTo("Full Name");
        assertThat(response.getFields().get(0).getType()).isEqualTo(FieldType.TEXT);
        assertThat(response.getFields().get(0).isRequired()).isTrue();
        assertThat(response.getFields().get(1).getName()).isEqualTo("email");
        assertThat(response.getFields().get(1).getType()).isEqualTo(FieldType.EMAIL);
    }

    // --- getForm ---

    @Test
    void getForm_existingId_returnsResponse() {
        when(formRepository.findById(FORM_ID)).thenReturn(Optional.of(savedForm));

        FormResponse response = formService.getForm(FORM_ID);

        assertThat(response.getId()).isEqualTo(FORM_ID);
        assertThat(response.getTitle()).isEqualTo(FORM_TITLE);
        assertThat(response.getFields()).hasSize(2);
    }

    @Test
    void getForm_unknownId_throwsFormNotFoundException() {
        when(formRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formService.getForm("bad-id"))
                .isInstanceOf(FormNotFoundException.class)
                .hasMessageContaining("bad-id");
    }

    // --- submitEntry ---

    @Test
    void submitEntry_validRequest_savesAndReturnsResponse() {
        when(formRepository.findById(FORM_ID)).thenReturn(Optional.of(savedForm));
        when(formEntryRepository.save(any(FormEntry.class))).thenReturn(savedEntry);

        EntryResponse response = formService.submitEntry(FORM_ID, buildEntryRequest());

        verify(entryValidator).validate(any(Form.class), any(EntryRequest.class));
        verify(formEntryRepository).save(any(FormEntry.class));
        assertThat(response.getId()).isEqualTo(ENTRY_ID);
        assertThat(response.getFormId()).isEqualTo(FORM_ID);
        assertThat(response.getAnswers()).containsEntry("name", "Jack Bauer");
        assertThat(response.getSubmittedAt()).isEqualTo(ENTRY_SUBMITTED_AT);
    }

    @Test
    void submitEntry_unknownFormId_throwsFormNotFoundException() {
        when(formRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formService.submitEntry("bad-id", buildEntryRequest()))
                .isInstanceOf(FormNotFoundException.class)
                .hasMessageContaining("bad-id");
    }

    @Test
    void submitEntry_validationFails_throwsEntryValidationException() {
        when(formRepository.findById(FORM_ID)).thenReturn(Optional.of(savedForm));
        doThrow(new EntryValidationException(List.of(new FieldError("email", "must be a valid email address"))))
                .when(entryValidator).validate(any(Form.class), any(EntryRequest.class));

        assertThatThrownBy(() -> formService.submitEntry(FORM_ID, buildEntryRequest()))
                .isInstanceOf(EntryValidationException.class);
    }

    // --- getEntries ---

    @Test
    void getEntries_existingForm_returnsMappedList() {
        when(formRepository.existsById(FORM_ID)).thenReturn(true);
        when(formEntryRepository.findAllByFormId(FORM_ID)).thenReturn(List.of(savedEntry));

        List<EntryResponse> responses = formService.getEntries(FORM_ID);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(ENTRY_ID);
        assertThat(responses.get(0).getFormId()).isEqualTo(FORM_ID);
        assertThat(responses.get(0).getAnswers()).containsEntry("email", "jack@example.com");
    }

    @Test
    void getEntries_unknownFormId_throwsFormNotFoundException() {
        when(formRepository.existsById("bad-id")).thenReturn(false);

        assertThatThrownBy(() -> formService.getEntries("bad-id"))
                .isInstanceOf(FormNotFoundException.class)
                .hasMessageContaining("bad-id");
    }

    @Test
    void getEntries_noSubmissions_returnsEmptyList() {
        when(formRepository.existsById(FORM_ID)).thenReturn(true);
        when(formEntryRepository.findAllByFormId(FORM_ID)).thenReturn(List.of());

        assertThat(formService.getEntries(FORM_ID)).isEmpty();
    }

    // --- helpers ---

    private FormRequest buildRequest() {
        FormFieldRequest nameField = new FormFieldRequest();
        nameField.setName("name");
        nameField.setLabel("Full Name");
        nameField.setType(FieldType.TEXT);
        nameField.setRequired(true);

        FormFieldRequest emailField = new FormFieldRequest();
        emailField.setName("email");
        emailField.setLabel("Email");
        emailField.setType(FieldType.EMAIL);
        emailField.setRequired(true);

        FormRequest request = new FormRequest();
        request.setTitle(FORM_TITLE);
        request.setDescription(FORM_DESCRIPTION);
        request.setFields(List.of(nameField, emailField));
        return request;
    }

    private EntryRequest buildEntryRequest() {
        EntryRequest request = new EntryRequest();
        request.setAnswers(Map.of("name", "Jack Bauer", "email", "jack@example.com"));
        return request;
    }
}
