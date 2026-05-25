package com.evently.service;

import com.evently.dto.FormFieldRequest;
import com.evently.dto.FormRequest;
import com.evently.dto.FormResponse;
import com.evently.exception.FormNotFoundException;
import com.evently.model.FieldType;
import com.evently.model.Form;
import com.evently.model.FormField;
import com.evently.repository.FormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private FormService formService;

    private Form savedForm;

    @BeforeEach
    void setUp() {
        savedForm = new Form(FORM_TITLE, FORM_DESCRIPTION, List.of(
                new FormField("name", "Full Name", FieldType.TEXT, true),
                new FormField("email", "Email", FieldType.EMAIL, true)
        ), FORM_CREATED_AT);
        savedForm.setId(FORM_ID);
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
}
